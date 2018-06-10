import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class DataMonitor implements Watcher {
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
            zooKeeper.exists("/znode_testowy", this);
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
            if (path.equals("/znode_testowy")) {
                try {
                    zooKeeper.getChildren("/znode_testowy", this);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
                listener.created(path);
            }
        } else if (event.getType() == Event.EventType.NodeDeleted) {
            System.out.println("deleted");
            listener.deleted(path);
        } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
            if (path.equals("/znode_testowy")) {
                try {
                    listener.childrenChanged(zooKeeper.getChildren("/znode_testowy", this).size());
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    @Override
//    public void processResult(int rc, String path, Object ctx, Stat stat) {
//        boolean exists;
//        switch (Code.get(rc)) {
//            case OK:
//                exists = true;
//                break;
//            case NONODE:
//                exists = false;
//                break;
//            case SESSIONEXPIRED:
//            case NOAUTH:
//                dead = true;
//                listener.closing(rc);
//                return;
//            default:
//                // Retry errors
//                zooKeeper.exists(znode, true, this, null);
//                return;
//        }
//
//        byte b[] = null;
//        if (exists) {
//            try {
//                b = zooKeeper.getData(znode, false, null);
//            } catch (KeeperException e) {
//                // We don't need to worry about recovering now. The watch
//                // callbacks will kick off any exception handling
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                return;
//            }
//        }
//        if ((b == null && b != prevData)
//                || (b != null && !Arrays.equals(prevData, b))) {
//            listener.exists(b);
//            prevData = b;
//        }
//    }
}
