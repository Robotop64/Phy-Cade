package kn.uni.util.fileRelated;

public class Permission
{
  public Level   current  = Level.User;
  public Level[] possible = Level.values();

  public enum Level
  {
    User, Developer
  }
}


