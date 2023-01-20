package kn.uni.util.fileRelated;

import java.util.HashMap;
import java.util.List;

public class DatabaseAccess
{
  HashMap <String, Database> databases = new HashMap <>();

  public DatabaseAccess ()
  { }
  //  addDatabase("example", new Database("exURL", "exUN", "exPW", Permission.Level.User));

  public void addDatabase (String name, Database database)
  {
    databases.put(name, database);
    JsonEditor.save(this, "DatabaseAccess");
  }

  public DatabaseAccess load ()
  {
    return (DatabaseAccess) JsonEditor.load(this, "DatabaseAccess");
  }

  public void save ()
  {
    JsonEditor.save(this, "DatabaseAccess");
  }

  public List <Database> getDatabases ()
  {
    return List.of(databases.values().toArray(new Database[0]));
  }

  public Database getMatchingPermissionDatabase (Permission.Level level)
  {
    for (Database database : databases.values())
    {
      if (database.level == level) return database;
    }
    return null;
  }

  public String getDatabaseName (Database database)
  {
    for (String name : databases.keySet())
    {
      if (databases.get(name) == database) return name;
    }
    return null;
  }
}
