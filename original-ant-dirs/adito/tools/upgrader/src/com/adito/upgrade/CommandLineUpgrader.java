
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.adito.upgrade;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommandLineUpgrader implements Upgrader {
    final static Log log = LogFactory.getLog(CommandLineUpgrader.class);
    
    private List upgrades;
    
    public CommandLineUpgrader(String[] args) throws Exception {

        File oldDir = new File(args[0]);
        if (!oldDir.exists() || !oldDir.isDirectory()) {
            System.err.println(oldDir.getAbsolutePath() + " does not exists or is not a directory");
            System.exit(1);
        }
        File newDir = new File(args[1]);
        if (!newDir.exists() || !newDir.isDirectory()) {
            System.err.println(newDir.getAbsolutePath() + " does not exists or is not a directory");
            System.exit(1);
        }
        if (oldDir.getCanonicalFile().equals(newDir.getCanonicalFile())) {
            System.err.println("Old and new installation directories are identical");
            System.exit(1);
        }
        if (!new File(newDir, "install").exists()) {
            System.err.println("New installation does not appear to be 0.2.5+");
            System.exit(1);
        }
        if (!new File(oldDir, "upgrade").exists()) {
            System.err.println("Old installation does not appear to be 0.1.15+");
            System.exit(1);
        }
        File oldDbDir = new File(oldDir, "db");
        File newDbDir = new File(newDir, "db");

        upgrades = new ArrayList();
        upgrades.add(new UserUpgrade(oldDbDir, newDbDir));
        upgrades.add(new AuthSchemeUpgrade(oldDbDir, newDbDir));
        upgrades.add(new TunnelsUpgrade(oldDbDir, newDbDir));
        upgrades.add(new NetworkPlacesUpgrade(oldDbDir, newDbDir));
        upgrades.add(new WebForwardsUpgrade(oldDbDir, newDbDir));
        upgrades.add(new IPRestrictionsUpgrade(oldDbDir, newDbDir));
        upgrades.add(new ApplicationShortcutsUpgrade(oldDbDir, newDbDir));
        upgrades.add(new ReplacementsUpgrade(oldDbDir, newDbDir));
    }

    public void error(String message) {
        log.error(message);

    }

    public void error(String message, Throwable exception) {
        log.error(message, exception);        
    }

    public void info(String message) {
        log.info(message);
    }

    public void upgrade() throws Exception {
        for (Iterator i = upgrades.iterator(); i.hasNext();) {
            try {
                AbstractDatabaseUpgrade upgrade = (AbstractDatabaseUpgrade)i.next();
                log.info("*** " + upgrade.getName() + " ***");
                log.info(upgrade.getDescription());
                if(yesno("Do you wish to run this upgrade?", upgrade.isSelectedByDefault())) {
                    upgrade.upgrade(this);
                }
                else {
                    log.info("Skipped " + upgrade.getName());
                }
            } catch (Exception e) {
                log.error("Failed upgrader.", e);
            }
        }

    }

    public void warn(String message) {
        log.warn(message);

    }

    public void warn(String message, Throwable exception) {
        log.warn(message, exception);
    }

    public static boolean yesno(String message, boolean yesByDefault) {
        while (true) {
            log.info(message + " (" + (yesByDefault ? "[Yes]" : "Yes") + " / " + (yesByDefault ? "No" : "[Yes]") + "): ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String l = br.readLine();
                if (l == null) {
                    return yesByDefault;
                }
                l = l.toLowerCase();
                if (l.trim().equals("")) {
                    return yesByDefault;
                }
                if (l.equals("y") || l.equals("yes")) {
                    return true;
                }
                if (l.equals("n") || l.equals("no")) {
                    return true;
                }
                log.error("Invalid reply.");
            }
            catch(IOException ioe) {
                return yesByDefault;
            }
        }
    }

}
