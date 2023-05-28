package com.lanstructor.android.model;

import java.io.Serializable;

public class Group implements Serializable {
   public String id;
   public String name;
   public String bio;
   public String mainLang;
   public Group(){}
   public Group(String id, String name, String bio, String mainLang) {
      this.id = id;
      this.name = name;
      this.bio = bio;
      this.mainLang = mainLang;
   }
}
