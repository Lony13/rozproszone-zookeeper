import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class StructurePrinter {
    private final ZooKeeper zooKeeper;

    public StructurePrinter(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void printStructure(String znode) throws KeeperException, InterruptedException {
        printStructure(znode, 0);
        System.out.println();
    }

    private void printStructure(String path, int indent) throws KeeperException, InterruptedException {
        System.out.print(StringUtil.getRepeated("\t", indent));
        System.out.println(path);

        for (String child : zooKeeper.getChildren(path, false)) {
            printStructure(path + "/" + child, indent + 1);
        }
    }
}
