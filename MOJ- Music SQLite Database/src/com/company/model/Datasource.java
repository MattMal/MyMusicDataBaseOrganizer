package com.company.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by owner on 2017-06-28.
 */
public class Datasource {
    public static final String DB_NAME = "music.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:/Users/owner/IdeaProjects/MOJ- Music SQLite Database/"+DB_NAME;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;


    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    public static final String TABLE_ARTIST_SONG_VIEW = "tunwas_playlist";


    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int  ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    public static final String SEARCH_SONGS_FOR_ARTIST = "SELECT "+ TABLE_ARTISTS+"."+COLUMN_ARTIST_NAME+ " from "+ TABLE_ARTISTS+ " inner join " + TABLE_ALBUMS +
            " on " + TABLE_ARTISTS+"."+COLUMN_ARTIST_ID + " = " + TABLE_ALBUMS +"." + COLUMN_ALBUM_ARTIST+ " inner join " +TABLE_SONGS+ " on "+ TABLE_SONGS+"." + COLUMN_SONG_ALBUM
            +"=" + TABLE_ALBUMS+"."+ COLUMN_ALBUM_ID + " where " + TABLE_SONGS+"."+COLUMN_SONG_TITLE+ "=\"";

    public static final String QUERY_VIEW_SONG_INFO_PREP = "SELECT " + COLUMN_ARTIST_NAME + ", " +
            COLUMN_SONG_ALBUM + ", " + COLUMN_SONG_TRACK + " FROM " + TABLE_ARTIST_SONG_VIEW +
            " WHERE " + COLUMN_SONG_TITLE + " = ?";


    private Connection conn;

    private PreparedStatement querySongInfoView;


    public boolean open(){
        try{
            conn = DriverManager.getConnection(CONNECTION_STRING);
            querySongInfoView = conn.prepareStatement(QUERY_VIEW_SONG_INFO_PREP);
            return true;
        } catch(SQLException e){
            System.out.println("Couldnt connect to database "+ e.getMessage());
            return false;
        }
    }

    public void close(){
        try{
            if(querySongInfoView != null){
                querySongInfoView.close();
            }
            if(conn != null){
                conn.close();
            }
        } catch(SQLException e){
            System.out.println("Couldnt close connection: "+e.getMessage());
        }
    }

    public List<Artist> queryArtists(int sortOrder){

        StringBuilder  sb = new StringBuilder("select * from ");
        sb.append( TABLE_ARTISTS );
        if(sortOrder != ORDER_BY_NONE){
            sb.append(" ORDER  BY ");
            sb.append(COLUMN_ARTIST_NAME);
            sb.append(" COLLATE NOCASE ");
            if(sortOrder == ORDER_BY_DESC){
                sb.append(" DESC ");
            } else {
                sb.append(" ASC ");
            }
        }

        try(Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sb.toString())){

            List<Artist> artists = new ArrayList<>();
            while(results.next()){
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }
            return artists;
        } catch (SQLException e){
            System.out.println("Query failed: "+e.getMessage());
            return null;

        }
    }

    public List<String> queryAlbumsForArtist(String artistName, int sortOrder){
        StringBuilder sb = new StringBuilder("Select ");
        sb.append(TABLE_ALBUMS);
        sb.append(".");
        sb.append(COLUMN_ALBUM_NAME);
        sb.append(" from " );
        sb.append(TABLE_ALBUMS);
        sb.append(" inner join ");
        sb.append(TABLE_ARTISTS);
        sb.append(" on ");
        sb.append(TABLE_ALBUMS);
        sb.append(".");
        sb.append(COLUMN_ALBUM_ARTIST);
        sb.append(" = ");
        sb.append(TABLE_ARTISTS);
        sb.append(".");
        sb.append(COLUMN_ARTIST_ID);
        sb.append(" where ");
        sb.append(TABLE_ARTISTS);
        sb.append(".");
        sb.append(COLUMN_ARTIST_NAME);
        sb.append(" = \"");
        sb.append(artistName);
        sb.append("\"");
        if(sortOrder != ORDER_BY_NONE ){
            sb.append(" order by ");
            sb.append(TABLE_ALBUMS);
            sb.append(".");
            sb.append(COLUMN_ALBUM_NAME);
            sb.append(" collate nocase ");
            if(sortOrder == ORDER_BY_DESC){
                sb.append(" desc ");
            } else{
                sb.append(" asc ");
            }


        }

        System.out.println("SQL statement = " + sb.toString());

        try(Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sb.toString())){

            List<String> albums = new ArrayList<>();
            while(results.next()){
                albums.add(results.getString(1));
            }
            return albums;

        } catch (SQLException e){
            System.out.println("Query failed: "+e.getMessage());
            return null;

        }



    }

    public List<String> queryArtistBySong(String songName, int sortOrder){

        StringBuilder sb = new StringBuilder(SEARCH_SONGS_FOR_ARTIST);
        sb.append(songName);
        sb.append("\"");
        if(sortOrder!= ORDER_BY_NONE){
            sb.append(" order by ");
            sb.append(TABLE_ALBUMS+"."+COLUMN_ALBUM_NAME);
            if(sortOrder == ORDER_BY_ASC){
                sb.append(" collate nocase ");
                sb.append(" ASC ");

            } else {
                sb.append("DESC");
            }
        }
        System.out.println(sb.toString());

        try(Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sb.toString())){
            List<String> artist = new ArrayList<>();
            while(results.next()){
                artist.add(results.getString(1));
            }
            return artist;
        } catch (SQLException e){
            System.out.println("Query Error: "+ e.getMessage());
            return null;
        }
    }

    public List<SongArtist> querySongInfoView(String title) {

        try {
            querySongInfoView.setString(1, title);
            ResultSet results = querySongInfoView.executeQuery();

            List<SongArtist> songArtists = new ArrayList<>();
            while (results.next()) {
                SongArtist songArtist = new SongArtist();
                songArtist.setArtistName(results.getString(1));
                songArtist.setAlbumName(results.getString(2));
                songArtist.setTrack(results.getInt(3));
                songArtists.add(songArtist);
            }

            return songArtists;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }



}
