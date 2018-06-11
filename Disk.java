package TermProject;

import java.util.HashMap;

/*
1. 문현균
2. 박민근
3. 박민근
4. 문현균
5. 손진우
6. 유승화
7. 유승화
8. 손진우
9. 이인홍
10. 이인홍
 */


//전역변수 처럼 사용하기 위함.
class Global {
    public static int diskID = 0;
    public static int iNodeID = 0;
    public static HashMap<Integer, INODE> iNodeHashMap = new HashMap<>(); //ID, INODE
}


class INODE {
    public Integer diskID;      //default null
    public String owner;        //default null
    public int startPoint;      //default -1
    public int size;            //default 0
    public boolean readOnly;    //default false

    INODE() {
        startPoint = -1;
        Global.iNodeHashMap.put(Global.iNodeID++, this);
    }
}

class SuperBlock {
    HashMap<Integer, Disk> diskHashMap;  //id, inode
    SuperBlock() {
        diskHashMap = new HashMap<>();
    }
}

public class Disk {
    HashMap<Integer, INODE> iNodeHashMap;  //id, inode
    int[][] blocks;

    //1. Initialize
    Disk(int numBlocks, int blockSize) {
        iNodeHashMap = new HashMap<>();
        blocks = new int[numBlocks][blockSize];
    }

    int getNumBlocks() {
        return blocks == null ? 0 : blocks.length;
    }

    int getBlockSize() {
        return getNumBlocks() > 0 ? blocks[0].length : 0;
    }

    //4. Empty space
    int getFreespace() {
        int total = getNumBlocks()*getBlockSize();

        for(HashMap.Entry<Integer, INODE> elem : iNodeHashMap.entrySet()){
            total -= elem.getValue().size;
        }

        return total;
    }

    public static void main(String[] args) {
        SuperBlock sb = new SuperBlock();

        Disk disk1 = new Disk(3, 4);
        sb.diskHashMap.put(Global.diskID++, disk1);
        Disk disk2 = new Disk(3, 4);
        sb.diskHashMap.put(Global.diskID++, disk2);
        System.out.println(sb.diskHashMap.keySet());

        INODE iNode1 = new INODE();
        INODE iNode2 = new INODE();
        System.out.println(iNode1.size);
        System.out.println(iNode1.diskID);
        System.out.println(iNode1.owner);
        System.out.println(iNode1.startPoint);
        System.out.println(iNode1.readOnly);
        System.out.println(Global.iNodeHashMap.keySet());
    }
}
