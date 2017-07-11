package com.company;

import com.company.model.Artist;
import com.company.model.Datasource;

import javax.sql.DataSource;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here

        Datasource ds = new Datasource();
        if(!ds.open()){
            System.out.println("cant open datasource");
            return;
        }
//        ds.close();


//        List<Artist> artistList = ds.queryArtists(Datasource.ORDER_BY_NONE);
//        if(artistList == null){
//            System.out.println("Artist list is empty");
//            return;
//        }
//
//        for(Artist artist : artistList){
//            System.out.println("ID = "+ artist.getId()+" Name = "+ artist.getName());
//        }

//        List<String> albumList = ds.queryAlbumsForArtist("Pink Floyd", 3);
//        if(albumList == null){
//            System.out.println("There are no albums for this artist");
//            return;
//        }
//        for(String album : albumList){
//            System.out.println("Album name = " + album);
//        }

        List<String> whoSangIt = ds.queryArtistBySong("Jesus Just Left Chicago", 2);
        if(whoSangIt == null){
            System.out.println("The song is not sang by anyone in the list");
            return;
        }

        for(String musician : whoSangIt){
            System.out.println("The song was sang by: "+ musician);
        }




    }
}
