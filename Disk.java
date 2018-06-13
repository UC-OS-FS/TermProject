package TermProject;

import java.util.Vector;
import java.util.Scanner;

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
    public static Vector<INODE> iNodeVector = new Vector<>();
    public static INODE getINODE(Integer id) {
        for (int i = 0; i < iNodeVector.size(); i++) {
            if (Integer.compare(iNodeVector.get(i).id, id) == 0)
                return iNodeVector.get(i);
        }
        return null;
    }
}


class INODE {
    public final Integer id;
    public Integer diskID;      //default null
    public String owner;        //default null
    public int startPoint;      //default -1
    public int size;            //default 0
    public boolean readOnly;    //default false

    INODE() {
        id = new Integer(Global.iNodeID++);
        startPoint = -1;
        Global.iNodeVector.add(this);
    }
}

class SuperBlock {
    Vector<Disk> diskVector;
    SuperBlock() {
        diskVector = new Vector<>();
    }
    public Disk getDisk(Integer id) {
        for (int i = 0; i < diskVector.size(); i++) {
            if (Integer.compare(diskVector.get(i).id, id) == 0)
                return diskVector.get(i);
        }
        return null;
    }
}

public class Disk {
    public final Integer id;
    Vector<Integer> iNodeIDVector;
    int[][] blocks;

    //1. Initialize
    Disk(int numBlocks, int blockSize) {
        id = new Integer(Global.diskID++);
        iNodeIDVector = new Vector<>();
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
        return getNumBlocks()*getBlockSize()
                - iNodeIDVector.stream().map(i -> Global.getINODE(i).size).mapToInt(i -> i).sum();
    }

    void Delete(Integer iNodeID) {
        iNodeIDVector.remove(iNodeIDVector.indexOf(iNodeID));
        /*
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the DISK and ID(diskid id):");
        int delID;
        int diskID;
        diskID = scan.nextInt();
        delID = scan.nextInt();
        boolean check = false;
        for(int i=0; i<SB.diskVector.size(); i++) {
            if(SB.diskVector.get(i).id == diskID) {
                for(int j=0; j<SB.diskVector.get(i).iNodeIDVector.size(); j++) {
                    if(SB.diskVector.get(i).iNodeIDVector.get(j) == delID) {
                        SB.diskVector.get(i).iNodeIDVector.remove(j);
                        check = true;
                    }
                }                
            }            
        }
        if(check == true) {
            System.out.println("Inode not found");
        }
        */
    }
    
    void Degfragment(SuperBlock SB) {
    
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the DISKID :");
        int diskID;
        diskID = scan.nextInt();
        
        for(int i=0; i<SB.diskVector.size(); i++) {
            if(SB.diskVector.get(i).id == diskID) {
                
            }            
        }
        /*
        for(int i=0; i<len; i++){
            if(i == 0){
                if(list.get(i) != 0){
                    int temp;
                    temp = fileMap.get(list.get(i));
                    fileMap.remove(list.get(i));
                    fileMap.put(0, temp);
                }           
               
            }
            else {
                if(list.get(i-1)+fileMap.get(list.get(i-1))+1 != list.get(i)) {
                     int temp;
                    temp = fileMap.get(list.get(i));
                    fileMap.remove(list.get(i));
                    fileMap.put(list.get(i-1)+fileMap.get(list.get(i-1))+1, temp);
                }
            }
            
        }
    */
    }

    public static void main(String[] args) {
        SuperBlock sb = new SuperBlock();

        // 디스크 생성
        Disk disk1 = new Disk(3, 4);
        Disk disk2 = new Disk(3, 4);

        // 디스크 삽입
        sb.diskVector.add(disk1);
        sb.diskVector.add(disk2);

        // iNode 생성
        INODE iNode1 = new INODE();
        INODE iNode2 = new INODE();

        // iNode 삽입
        sb.diskVector.firstElement().iNodeIDVector.add(iNode1.id);
        sb.diskVector.firstElement().iNodeIDVector.add(iNode2.id);

        // create 대체
        iNode1.startPoint = 0;
        iNode1.size = 7;
        iNode2.startPoint = 9;
        iNode2.size = 1;

        System.out.println(sb.diskVector.firstElement().getFreespace());
        System.out.println(sb.diskVector.lastElement().getFreespace());

        // iNode의 정보를 바꾸었을때 sb에서 iNode의 id를 가지고 온 후 Global에서 조회
        iNode1.owner = "java";
        iNode2.owner = "os";
        for (int i = 0; i < sb.diskVector.firstElement().iNodeIDVector.size(); i++) {
            System.out.println(Global.getINODE(sb.diskVector.firstElement().iNodeIDVector.get(i)).owner);
        }
    }
}
