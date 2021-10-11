package K_means;

import static K_means.Main.*;

public class Thread2 extends Thread {
    @Override
    public void run() {
        for (int i = 1; i < COUNT_CLUSTERS; i+=2) {

            double res;
            int clusterSize = clusters[i].size();
            for (int n = 0; n < COUNT_ATTRIBUTES; n++) {
                res = 0;
                for (int k = 0; k < clusterSize; k++) {
                    res += ((Element) clusters[i].get(k)).attribute[n];
                }
                res /= clusterSize;
                centroidsTemp[i].attribute[n] = centroids[i].attribute[n];
                centroids[i].attribute[n] = res;
            }
        }
    }
}
