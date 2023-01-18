package kn.uni.util.fileRelated;

public class Permission
{
  public Level current = Level.User;
  Level[] possible = Level.values();

  enum Level
  {
    User, Developer
  }
}
