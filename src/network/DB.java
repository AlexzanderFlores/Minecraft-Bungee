package network;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public enum DB {
	PLAYERS_ACCOUNTS("uuid VARCHAR(40), name VARCHAR(16), address VARCHAR(40), rank VARCHAR(20), join_time VARCHAR(10), PRIMARY KEY(uuid)"),
	
	NETWORK_PROXIES("server varchar(10), PRIMARY KEY(server)"),
	NETWORK_POPULATIONS("server varchar(10), population INT, PRIMARY KEY(server)"),
	NETWORK_COMMAND_DISPATCHER("id INT NOT NULL AUTO_INCREMENT, server varchar(10), command varchar(250), PRIMARY KEY(id)"),
	NETWORK_SERVER_LIST("data_type varchar(15), data_value varchar(100), PRIMARY KEY(data_type)"),
	NETWORK_SERVER_STATUS("id INT NOT NULL AUTO_INCREMENT, game_name VARCHAR(25), server_number INT, listed_priority INT, lore VARCHAR(100), players INT, max_players INT, PRIMARY KEY(id)");
	
	private String table = null;
	private String keys = "";
	private Databases database = null;
	
	private DB(String query) {
		String databaseName = toString().split("_")[0];
		database = Databases.valueOf(databaseName);
		table = toString().replaceAll(databaseName, "");
		table = table.substring(1, table.length()).toLowerCase();
		String [] declarations = query.split(", ");
		for(int a = 0; a < declarations.length - 1; ++a) {
			String declaration = declarations[a].split(" ")[0];
			if(!declaration.equals("id")) {
				keys += "`" + declaration + "`, ";
			}
		}
		keys = keys.substring(0, keys.length() - 2);
		database.connect();
		try {
			if(database.getConnection() != null) {
				database.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " (" + query + ")").execute();
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		}
	}
	
	public String getName() {
		return table;
	}
	
	public Connection getConnection() {
		return this.database.getConnection();
	}
	
	public boolean isKeySet(String key, String value) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = getConnection().prepareStatement("SELECT COUNT(" + key + ") FROM " + getName() + " WHERE " + key + " = '" + value + "' LIMIT 1");
			resultSet = statement.executeQuery();
			return resultSet.next() && resultSet.getInt(1) > 0;
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return false;
	}
	
	public boolean isKeySet(String [] keys, String [] values) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String query = "SELECT COUNT(" + keys[0] + ") FROM " + getName() + " WHERE " + keys[0] + " = '" + values[0] + "'";
			for(int a = 1; a < keys.length; ++a) {
				query += " AND " + keys[a] + " = '" + values[a] + "'";
			}
			statement = getConnection().prepareStatement(query + " LIMIT 1");
			resultSet = statement.executeQuery();
			return resultSet.next() && resultSet.getInt(1) > 0;
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return false;
	}
	
	public boolean isUUIDSet(UUID uuid) {
		return isUUIDSet("uuid", uuid);
	}
	
	public boolean isUUIDSet(String key, UUID uuid) {
		return isKeySet(key, uuid.toString());
	}
	
	public int getInt(String key, String value, String requested) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = getConnection().prepareStatement("SELECT " + requested + " FROM " + getName() + " WHERE " + key + " = '" + value + "' LIMIT 1");
			resultSet = statement.executeQuery();
			if(resultSet.next()) {
				return resultSet.getInt(requested);
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return 0;
	}
	
	public int getInt(String [] keys, String [] values, String requested) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String query = "SELECT " + requested + " FROM " + getName() + " WHERE " + keys[0] + " = '" + values[0] + "'";
			for(int a = 1; a < keys.length; ++a) {
				query += " AND " + keys[a] + " = '" + values[a] + "'";
			}
			statement = getConnection().prepareStatement(query);
			resultSet = statement.executeQuery();
			if(resultSet.next()) {
				return resultSet.getInt(requested);
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return 0;
	}
	
	public void updateInt(String set, int update, String key, String value) {
		PreparedStatement statement = null;
		try {
			statement = getConnection().prepareStatement("UPDATE " + getName() + " SET " + set + " = '" + update + "' WHERE " + key + " = '" + value + "'");
			statement.execute();
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement);
		}
	}
	
	public void updateInt(String set, int update, String [] keys, String [] values) {
		PreparedStatement statement = null;
		try {
			String query = "UPDATE " + getName() + " SET " + set + " = '" + update + "' WHERE " + keys[0] + " = '" + values[0] + "'";
			for(int a = 0; a < keys.length; ++a) {
				query += " AND " + keys[a] + " = '" + values[a] + "'";
			}
			statement = getConnection().prepareStatement(query);
			statement.execute();
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement);
		}
	}
	
	public String getString(String key, String value, String requested) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = getConnection().prepareStatement("SELECT " + requested + " FROM " + getName() + " WHERE " + key + " = '" + value + "' LIMIT 1");
			resultSet = statement.executeQuery();
			if(resultSet.next()) {
				return resultSet.getString(requested);
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return null;
	}
	
	public String getString(String [] keys, String [] values, String requested) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String query = "SELECT " + requested + " FROM " + getName() + " WHERE " + keys[0] + " = '" + values[0] + "'";
			for(int a = 1; a < keys.length; ++a) {
				query += " AND " + keys[a] + " = '" + values[a] + "'";
			}
			statement = getConnection().prepareStatement(query + " LIMIT 1");
			resultSet = statement.executeQuery();
			if(resultSet.next()) {
				return resultSet.getString(requested);
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return null;
	}
	
	public List<String> getAllStrings(String colum) {
		return getAllStrings(colum, null, null);
	}
	
	public List<String> getAllStrings(String colum, String key, String value) {
		List<String> results = new ArrayList<String>();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String query = "SELECT " + colum + " FROM " + getName();
			if(key != null && value != null) {
				query += " WHERE " + key + " = '" + value + "'";
			}
			statement = getConnection().prepareStatement(query);
			resultSet = statement.executeQuery();
			while(resultSet.next()) {
				results.add(resultSet.getString(colum));
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return results;
	}
	
	public void updateString(String set, String update, String key, String value) {
		PreparedStatement statement = null;
		try {
			statement = getConnection().prepareStatement("UPDATE " + getName() + " SET " + set + " = '" + update + "' WHERE " + key + " = '" + value + "'");
			statement.execute();
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement);
		}
	}
	
	public void updateString(String set, String update, String [] keys, String [] values) {
		PreparedStatement statement = null;
		try {
			String query = "UPDATE " + getName() + " SET " + set + " = '" + update + "' WHERE " + keys[0] + " = '" + values[0] + "'";
			for(int a = 1; a < keys.length; ++a) {
				query += " AND " + keys[a] + " = '" + values[a] + "'";
			}
			statement = getConnection().prepareStatement(query);
			statement.execute();
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement);
		}
	}
	
	public boolean getBoolean(String key, String value, String requested) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = getConnection().prepareStatement("SELECT " + requested + " FROM " + getName() + " WHERE " + key + " = '" + value + "'");
			resultSet = statement.executeQuery();
			return resultSet.next() && resultSet.getBoolean(requested);
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return false;
	}
	
	public int getSize() {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = getConnection().prepareStatement("SELECT COUNT(*) FROM " + getName());
			resultSet = statement.executeQuery();
			if(resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return 0;
	}
	
	public int getSize(String key, String value) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = getConnection().prepareStatement("SELECT COUNT(" + key + ") FROM " + getName() + " WHERE " + key + " = '" + value + "'");
			resultSet = statement.executeQuery();
			if(resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return 0;
	}
	
	public int getSize(String [] keys, String [] values) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String query = "SELECT COUNT(" + keys[0] + ") FROM " + getName() + " WHERE " + keys[0] + " = '" + values[0] + "'";
			for(int a = 1; a < keys.length; ++a) {
				query += " AND " + keys[a] + " = '" + values[a] + "'";
			}
			statement = getConnection().prepareStatement(query);
			resultSet = statement.executeQuery();
			if(resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return 0;
	}
	
	public List<String> getOrdered(String orderBy, String requested, String key, String value, int limit) {
		return getOrdered(orderBy, requested, key, value, limit, false);
	}
	
	public List<String> getOrdered(String orderBy, String requested, String key, String value, int limit, boolean descending) {
		List<String> results = new ArrayList<String>();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String desc = descending ? " DESC " : " ASC ";
			String max = limit > 0 ? " LIMIT " + limit : "";
			statement = getConnection().prepareStatement("SELECT " + requested + " FROM " + getName() + " WHERE " + key + " = '" + value + "' ORDER BY " + orderBy + desc + max);
			resultSet = statement.executeQuery();
			while(resultSet.next()) {
				results.add(resultSet.getString(requested));
			}
			return results;
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return null;
	}
	
	public List<String> getOrdered(String orderBy, String requested, int limit) {
		return getOrdered(orderBy, requested, limit, false);
	}
	
	public List<String> getOrdered(String orderBy, String requested, int limit, boolean descending) {
		List<String> results = new ArrayList<String>();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String desc = descending ? " DESC " : " ASC ";
			String max = limit > 0 ? " LIMIT " + limit : "";
			statement = getConnection().prepareStatement("SELECT " + requested + " FROM " + getName() + " ORDER BY " + orderBy + desc + max);
			resultSet = statement.executeQuery();
			while(resultSet.next()) {
				results.add(resultSet.getString(requested));
			}
			return results;
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement, resultSet);
		}
		return null;
	}
	
	public void delete(String key, String value) {
		PreparedStatement statement = null;
		try {
			statement = getConnection().prepareStatement("DELETE FROM " + getName() + " WHERE " + key + " = '" + value + "'");
			statement.execute();
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement);
		}
	}
	
	public void delete(String [] keys, String [] values) {
		PreparedStatement statement = null;
		try {
			String query = "DELETE FROM " + getName() + " WHERE " + keys[0] + " = '" + values[0] + "'";
			for(int a = 1; a < keys.length; ++a) {
				query += " AND " + keys[a] + " = '" + values[a] + "'";
			}
			statement = getConnection().prepareStatement(query);
			statement.execute();
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement);
		}
	}
	
	public void deleteUUID(UUID uuid) {
		deleteUUID("uuid", uuid);
	}
	
	public void deleteUUID(String key, UUID uuid) {
		delete(key, uuid.toString());
	}
	
	public void insert(String values) {
		PreparedStatement statement = null;
		try {
			statement = getConnection().prepareStatement("INSERT INTO " + getName() + " (" + keys + ") VALUES (" + values + ")");
			statement.execute();
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		} finally {
			close(statement);
		}
	}
	
	public static void close(PreparedStatement statement, ResultSet resultSet) {
		close(statement);
		close(resultSet);
	}
	
	public static void close(PreparedStatement statement) {
		try {
			if(statement != null) {
				statement.close();
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		}
	}
	
	public static void close(ResultSet resultSet) {
		try {
			if(resultSet != null) {
				resultSet.close();
			}
		} catch(SQLException e) {
			BungeeCord.getInstance().getLogger().info(e.getMessage());
		}
	}
	
	public enum Databases {
		PLAYERS, NETWORK, HUB, STAFF;
		
		private Connection connection = null;
		
		public void connect() {
			if(connection == null) {
				try {
					String path = Network.getInstance().getDataFolder() + "/db.yml";
					Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(path));
					String address = config.getString("address");
					int port = config.getInt("port");
					String user = config.getString("user");
					String password = config.getString("password");
					connection = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + toString().toLowerCase(), user, password);
				} catch(Exception e) {
					BungeeCord.getInstance().getLogger().info(e.getMessage());
				}
			}
		}
		
		public Connection getConnection() {
			return this.connection;
		}
		
		public void disconnect() {
			if(connection != null) {
				try {
					connection.close();
				} catch(SQLException e) {
					BungeeCord.getInstance().getLogger().info(e.getMessage());
				}
			}
		}
	}
}