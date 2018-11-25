package Model.Algos;

import Model.Process;

import java.util.LinkedList;
import java.util.Optional;

public class BuddyAlgo implements Algo {
    private LinkedList<Block> memory;
    private String name;
    public BuddyAlgo(int memSize){
        memory=new LinkedList<>();
        memory.add(new Block(0,memSize));
        name = "Buddy";
    }
    @Override
    public Integer allocPs(Process unallocated) {
        for(Block b: memory){
            if(b.getLength()>unallocated.getSize()){
                int base=b.getBase();
                b.setBase(b.getBase()+unallocated.getSize());
                unallocated.setBaseAddress(Optional.of(new Integer(base)));
                return base;
            }
        }
        return null;
    }

    @Override
    public boolean deallocate(Process allocated) {
        Block b=new Block(allocated.getBaseAddress().get(),allocated.getSize());
        merge(b);
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    private void merge(Block newBlock){
        for(Block b:memory){
            if(b.getBase()==newBlock.getBase()+newBlock.getLength()){
                b.setBase(newBlock.getBase());
                return;
            }
            else if(b.getBase()+b.getLength()==newBlock.getBase()){
                b.setLength(b.getLength()+newBlock.getLength());
                return;
            }
        }
        memory.add(newBlock);
    }
    private class Block{
        int base;
        int length;

        public Block(int base, int length) {
            this.base = base;
            this.length = length;
        }

        public int getBase() {
            return base;
        }

        public void setBase(int base) {
            this.base = base;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

    }
}
