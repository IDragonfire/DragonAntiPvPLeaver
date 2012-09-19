package com.topcat.npclib.DragonAntiPvPListener.pathing;

import java.util.ArrayList;

import org.bukkit.Location;

public class NPCPath {

    private ArrayList<Node> path;
    private NPCPathFinder pathFinder;
    private Location end;

    public NPCPath(NPCPathFinder npcPathFinder, ArrayList<Node> path,
            Location end) {
        this.path = path;
        this.end = end;
        this.pathFinder = npcPathFinder;
    }

    public Location getEnd() {
        return this.end;
    }

    public ArrayList<Node> getPath() {
        return this.path;
    }

    public boolean checkPath(Node node, Node parent, boolean update) {
        return this.pathFinder.checkPath(node, parent, update);
    }

}