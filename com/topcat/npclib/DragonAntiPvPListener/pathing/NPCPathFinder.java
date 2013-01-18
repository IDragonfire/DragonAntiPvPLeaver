package com.topcat.npclib.DragonAntiPvPListener.pathing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * 
 * @author Top_Cat
 */
public class NPCPathFinder extends Thread {

    HashMap<Block, Node> nodes = new HashMap<Block, Node>();
    ArrayList<Node> path = new ArrayList<Node>();
    ArrayList<Node> open = new ArrayList<Node>();
    ArrayList<Node> closed = new ArrayList<Node>();
    Comparator<Node> nodeComp = new NodeComparator();
    Node startNode, endNode;
    public boolean cancel = false;
    private Location start, end;
    private int maxIterations;
    private PathReturn callback;

    public NPCPathFinder(Location start, Location end, int maxIterations,
            PathReturn callback) {
        this.start = start;
        this.end = end;
        this.maxIterations = maxIterations;
        this.callback = callback;
    }

    @Override
    public void run() {
        this.startNode = getNode(this.start.getBlock());
        this.endNode = getNode(this.end.getBlock());
        look(this.startNode, this.maxIterations);
        if (this.cancel) {
            this.path.clear();
        }
        this.callback.run(new NPCPath(this, this.path, this.end));
    }

    private Node getNode(Block b) {
        if (!this.nodes.containsKey(b)) {
            this.nodes.put(b, new Node(b));
        }
        return this.nodes.get(b);
    }

    private void look(Node c, int max) {
        Node adjacentBlock;
        int rep = 0;
        while (c != this.endNode && rep < max) { // Repetition variable prevents infinite loop when destination is unreachable
            if (this.cancel) {
                return;
            }
            rep++;
            this.closed.add(c);
            this.open.remove(c);

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        adjacentBlock = getNode(c.b.getRelative(i, j, k));
                        if (adjacentBlock != c
                                && !(j == 1 && adjacentBlock.b.getRelative(0,
                                        -1, 0).getType() == Material.FENCE)) {
                            scoreBlock(adjacentBlock, c);
                        }
                    }
                }
            }
            Node[] n = this.open.toArray(new Node[this.open.size()]);
            Arrays.sort(n, this.nodeComp);
            if (n.length == 0) {
                break;
            }
            c = n[0];
            if (c == this.endNode) {
                adjacentBlock = c;
                while (adjacentBlock != null && adjacentBlock != this.startNode) {
                    this.path.add(adjacentBlock);
                    adjacentBlock = adjacentBlock.parent;
                }
                Collections.reverse(this.path);
            }
        }
        if (this.path.size() == 0) {
            this.path.add(this.endNode);
        }
    }

    public class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            if (o1.f > o2.f) {
                return 1;
            } else if (o1.f < o2.f) {
                return -1;
            }
            return 0;
        }
    }

    public boolean checkPath(Node node, Node parent) {
        return checkPath(node, parent, false);
    }

    public boolean checkPath(Node node, Node parent, boolean update) {
        boolean corner = false;
        if (node.xPos != parent.xPos && node.zPos != parent.zPos) {
            int xDir = node.xPos - parent.xPos;
            int zDir = node.zPos - parent.zPos;

            boolean xZCor1 = !getNode(parent.b.getRelative(0, 0, zDir)).notsolid;
            boolean xZCor2 = !getNode(parent.b.getRelative(xDir, 0, 0)).notsolid;

            corner = xZCor1 || xZCor2;
        } else if (node.xPos != parent.xPos && node.yPos != parent.yPos
                || node.yPos != parent.yPos && node.zPos != parent.zPos) {
            corner = node.yPos > parent.yPos ? !getNode(parent.b.getRelative(0,
                    2, 0)).notsolid
                    : !getNode(node.b.getRelative(0, 2, 0)).notsolid;
            ;
        }

        Node nodeBelow = getNode(node.b.getRelative(0, -1, 0));
        Node nodeAbove = getNode(node.b.getRelative(0, 1, 0));

        if (update) {
            nodeBelow.update();
            nodeAbove.update();
            node.update();
        }

        return !corner
                && (node.notsolid
                        && (!nodeBelow.notsolid || nodeBelow.liquid
                                && node.liquid) && nodeAbove.notsolid || node == this.endNode);
    }

    private void scoreBlock(Node node, Node parent) {
        int diagonal = node.xPos != parent.xPos && node.zPos != parent.zPos
                || node.xPos != parent.xPos && node.yPos != parent.yPos
                || node.yPos != parent.yPos && node.zPos != parent.zPos ? 14
                : 10;

        if (checkPath(node, parent)) {
            if (!this.open.contains(node) && !this.closed.contains(node)) {
                node.parent = parent;
                node.g = parent.g + diagonal;

                int difX = Math.abs(this.endNode.xPos - node.xPos);
                int difY = Math.abs(this.endNode.yPos - node.yPos);
                int difZ = Math.abs(this.endNode.zPos - node.zPos);

                node.h = (difX + difY + difZ) * 10;
                node.f = node.g + node.h;

                this.open.add(node);
            } else if (!this.closed.contains(node)) {
                int g = parent.g + diagonal;
                if (g < node.g) {
                    node.g = g;
                    node.parent = parent;
                }
            }
        }
    }

}
