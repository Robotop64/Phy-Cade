package kn.uni.util.fileRelated;

public class Database
{
  public String url;
  public String username;
  public String password;
  Permission.Level level;

  public Database (String url, String username, String password, Permission.Level level)
  {
    this.url = url;
    this.username = username;
    this.password = password;
    this.level = level;
  }
}
