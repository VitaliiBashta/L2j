package com.l2jserver.gameserver.idfactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.util.file.filter.PrimeFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BitSetIDFactory extends IdFactory {

  private static final Logger LOG = LoggerFactory.getLogger(BitSetIDFactory.class);
  private final ThreadPoolManager threadPoolManager;
  private BitSet freeIds;
  private AtomicInteger _freeIdCount;
  private AtomicInteger _nextFreeId;

  protected BitSetIDFactory(
      ConnectionFactory connectionFactory, ThreadPoolManager threadPoolManager) {
    super(connectionFactory);
    this.threadPoolManager = threadPoolManager;
  }

  @PostConstruct
  public void initialize() {
    this.threadPoolManager.scheduleGeneralAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);

    try {
      freeIds = new BitSet(PrimeFinder.nextPrime(100000));
      freeIds.clear();
      _freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);

      for (int usedObjectId : extractUsedObjectIDTable()) {
        int objectID = usedObjectId - FIRST_OID;
        if (objectID < 0) {
          LOG.warn("Object Id {} in DB is less than minimum ID of {}!", usedObjectId, FIRST_OID);
          continue;
        }
        freeIds.set(usedObjectId - FIRST_OID);
        _freeIdCount.decrementAndGet();
      }

      _nextFreeId = new AtomicInteger(freeIds.nextClearBit(0));
      _initialized = true;
    } catch (Exception ex) {
      _initialized = false;
      LOG.error("Could not be initialized properly!", ex);
    }
  }

  @Override
  public synchronized int getNextId() {
    int newID = _nextFreeId.get();
    freeIds.set(newID);
    _freeIdCount.decrementAndGet();

    int nextFree = freeIds.nextClearBit(newID);

    if (nextFree < 0) {
      nextFree = freeIds.nextClearBit(0);
    }
    if (nextFree < 0) {
      if (freeIds.size() < FREE_OBJECT_ID_SIZE) {
        increaseBitSetCapacity();
      } else {
        throw new NullPointerException("Ran out of valid Id's.");
      }
    }

    _nextFreeId.set(nextFree);

    return newID + FIRST_OID;
  }

  @Override
  public synchronized void releaseId(int objectId) {
    if ((objectId - FIRST_OID) > -1) {
      freeIds.clear(objectId - FIRST_OID);
      _freeIdCount.incrementAndGet();
    } else {
      LOG.warn("Release objectID {} failed (< {}).", objectId, FIRST_OID);
    }
  }

  @Override
  public synchronized int size() {
    return _freeIdCount.get();
  }

  protected synchronized void increaseBitSetCapacity() {
    BitSet newBitSet = new BitSet(PrimeFinder.nextPrime((usedIdCount() * 11) / 10));
    newBitSet.or(freeIds);
    freeIds = newBitSet;
  }

  protected synchronized int usedIdCount() {
    return (size() - FIRST_OID);
  }

  protected synchronized boolean reachingBitSetCapacity() {
    return PrimeFinder.nextPrime((usedIdCount() * 11) / 10) > freeIds.size();
  }

  protected class BitSetCapacityCheck implements Runnable {
    @Override
    public void run() {
      synchronized (BitSetIDFactory.this) {
        if (reachingBitSetCapacity()) {
          increaseBitSetCapacity();
        }
      }
    }
  }
}
