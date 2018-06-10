import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class DataMonitor implements Watcher {
    static final String ZNODE = "/znode_testowy";

    private ZooKeeper zooKeeper;
    private DataMonitorListener listener;
    boolean dead;

    public DataMonitor(ZooKeeper zooKeeper, DataMonitorListener listener) {
        this.listener = listener;
        this.zooKeeper = zooKeeper;
    }

    public interface DataMonitorListener {
        void created(String path);

        void deleted(String path);

        void childrenChanged(int size);

        void closing();
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.toString());
        String path = event.getPath();
        try {
            zooKeeper.exists(ZNODE, this);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        if (event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case Expired:
                    dead = true;
                    listener.closing();
                    break;
            }
        } else if (event.getType() == Event.EventType.NodeCreated) {
            System.out.println("created");
            if (path.equals(ZNODE)) {
                try {
                    zooKeeper.getChildren(ZNODE, this);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
                listener.created(path);
            }
        } else if (event.getType() == Event.EventType.NodeDeleted) {
            System.out.println("deleted");
            listener.deleted(path);
        } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
            if (path.equals(ZNODE)) {
                try {
                    listener.childrenChanged(zooKeeper.getChildren(ZNODE, this).size());
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
