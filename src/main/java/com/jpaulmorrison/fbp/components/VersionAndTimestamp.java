/*
 * JavaFBP-WebSockets - JavaFBP code supporting web sockets
 * Copyright (C) 2009, 2020 J. Paul Morrison
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *
 */
package com.jpaulmorrison.fbp.components; 


public final class VersionAndTimestamp {
	
	/**
	 * This class is simply used to record the version number and timestamp of the last update
	 *   
	 */

  private static String version = "JavaFBP-WebSockets - version 1.2.8";

  private static String date = "June 5, 2021";

  static String getVersion() {
    return version;
  }

  static String getDate() {
    return date;
  }
}
