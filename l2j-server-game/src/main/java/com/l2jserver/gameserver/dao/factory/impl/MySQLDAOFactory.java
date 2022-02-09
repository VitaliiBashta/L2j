package com.l2jserver.gameserver.dao.factory.impl;

import com.l2jserver.gameserver.bbs.repository.ForumRepository;
import com.l2jserver.gameserver.bbs.repository.PostRepository;
import com.l2jserver.gameserver.bbs.repository.TopicRepository;
import com.l2jserver.gameserver.bbs.repository.impl.ForumRepositoryMySQLImpl;
import com.l2jserver.gameserver.bbs.repository.impl.PostRepositoryMySQLImpl;
import com.l2jserver.gameserver.bbs.repository.impl.TopicRepositoryMySQLImpl;
import com.l2jserver.gameserver.dao.*;
import com.l2jserver.gameserver.dao.factory.IDAOFactory;
import com.l2jserver.gameserver.dao.impl.mysql.*;

enum MySQLDAOFactory implements IDAOFactory {
  INSTANCE;

  private final FriendDAO friendDAO = new FriendDAOMySQLImpl();
  private final HennaDAO hennaDAO = new HennaDAOMySQLImpl();
  private final ItemDAO itemDAO = new ItemDAOMySQLImpl();
  private final ItemReuseDAO itemReuseDAO = new ItemReuseDAOMySQLImpl();
  private final PetDAO petDAO = new PetDAOMySQLImpl();
  private final PetSkillSaveDAO petSkillSaveDAO = new PetSkillSaveDAOMySQL();
  private final PlayerDAO playerDAO = new PlayerDAOMySQLImpl();
  private final PlayerSkillSaveDAO playerSkillSaveDAO = new PlayerSkillSaveDAOMySQLImpl();
  private final PremiumItemDAO premiumItemDAO = new PremiumItemDAOMySQLImpl();
  private final RecipeBookDAO recipeBookDAO = new RecipeBookDAOMySQLImpl();
  private final RecipeShopListDAO recipeShopListDAO = new RecipeShopListDAOMySQLImpl();
  private final RecommendationBonusDAO recommendationBonusDAO =
      new RecommendationBonusDAOMySQLImpl();
  private final ServitorSkillSaveDAO servitorSkillSaveDAO = new ServitorSkillSaveDAOMySQLImpl();
  private final ShortcutDAO shortcutDAO = new ShortcutDAOMySQLImpl();
  private final SkillDAO skillDAO = new SkillDAOMySQLImpl();
  private final SubclassDAO subclassDAO = new SubclassDAOMySQLImpl();
  private final TeleportBookmarkDAO teleportBookmarkDAO = new TeleportBookmarkDAOMySQLImpl();
  private final ClanDAO clanDAO = new ClanDAOMySQLImpl();
  private final ForumRepository forumRepository = new ForumRepositoryMySQLImpl();
  private final TopicRepository topicRepository = new TopicRepositoryMySQLImpl();
  private final PostRepository postRepository = new PostRepositoryMySQLImpl();

  @Override
  public FriendDAO getFriendDAO() {
    return friendDAO;
  }

  @Override
  public HennaDAO getHennaDAO() {
    return hennaDAO;
  }

  @Override
  public ItemDAO getItemDAO() {
    return itemDAO;
  }

  @Override
  public ItemReuseDAO getItemReuseDAO() {
    return itemReuseDAO;
  }

  @Override
  public PetDAO getPetDAO() {
    return petDAO;
  }

  @Override
  public PetSkillSaveDAO getPetSkillSaveDAO() {
    return petSkillSaveDAO;
  }

  @Override
  public PlayerDAO getPlayerDAO() {
    return playerDAO;
  }

  @Override
  public PlayerSkillSaveDAO getPlayerSkillSaveDAO() {
    return playerSkillSaveDAO;
  }

  @Override
  public PremiumItemDAO getPremiumItemDAO() {
    return premiumItemDAO;
  }

  @Override
  public RecipeBookDAO getRecipeBookDAO() {
    return recipeBookDAO;
  }

  @Override
  public RecipeShopListDAO getRecipeShopListDAO() {
    return recipeShopListDAO;
  }

  @Override
  public RecommendationBonusDAO getRecommendationBonusDAO() {
    return recommendationBonusDAO;
  }

  @Override
  public ServitorSkillSaveDAO getServitorSkillSaveDAO() {
    return servitorSkillSaveDAO;
  }

  @Override
  public ShortcutDAO getShortcutDAO() {
    return shortcutDAO;
  }

  @Override
  public SkillDAO getSkillDAO() {
    return skillDAO;
  }

  @Override
  public SubclassDAO getSubclassDAO() {
    return subclassDAO;
  }

  @Override
  public TeleportBookmarkDAO getTeleportBookmarkDAO() {
    return teleportBookmarkDAO;
  }

  @Override
  public ClanDAO getClanDAO() {
    return clanDAO;
  }

  @Override
  public ForumRepository getForumRepository() {
    return forumRepository;
  }

  @Override
  public TopicRepository getTopicRepository() {
    return topicRepository;
  }

  @Override
  public PostRepository getPostRepository() {
    return postRepository;
  }
}
