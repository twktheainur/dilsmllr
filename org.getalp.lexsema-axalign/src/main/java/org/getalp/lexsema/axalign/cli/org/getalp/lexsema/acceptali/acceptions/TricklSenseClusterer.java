package org.getalp.lexsema.axalign.cli.org.getalp.lexsema.acceptali.acceptions;


import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.trickl.cluster.ClusterAlgorithm;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.ml.matrix.filters.Filter;

import java.util.ArrayList;
import java.util.List;

public class TricklSenseClusterer implements SenseClusterer {
    private final ClusterAlgorithm clusterAlgorithm;
    private Filter kernelFilter;
    private final double threshold;

    public TricklSenseClusterer(ClusterAlgorithm clusterAlgorithm) {
        this(clusterAlgorithm, 0);

    }

    public TricklSenseClusterer(ClusterAlgorithm clusterAlgorithm, double threshold) {
        this.clusterAlgorithm = clusterAlgorithm;
        this.threshold = threshold;
    }

    @Override
    public List<SenseCluster> cluster(DoubleMatrix2D data, int numClusters, List<Sense> senses){
        DoubleMatrix2D filteredData = data;
        if(kernelFilter!=null) {
            filteredData = kernelFilter.apply(data);
        }
        clusterAlgorithm.cluster(filteredData, numClusters);
        DoubleMatrix2D assignment = clusterAlgorithm.getPartition();
        return  extractClusters(assignment,senses, data);
    }

    private List<SenseCluster> extractClusters(DoubleMatrix2D assignments, List<Sense> senses, DoubleMatrix2D data){
        List<SenseCluster> clusters = new ArrayList<>();

        for(int col=0;col<assignments.columns();col++){
                clusters.add(new SenseClusterImpl(String.valueOf(col+1)));
        }
        for(int row=0;row<assignments.rows();row++){
            for(int col=0;col<assignments.columns();col++){
                if (assignments.getQuick(row, col) > threshold) {
                    clusters.get(col).addMember(senses.get(row),data.viewRow(row),assignments.getQuick(row,col));
                }
            }
        }
        return clusters;
    }

    @SuppressWarnings("all")
    public void clearFilter(){
        kernelFilter = null;
    }

    @Override
    public void setKernelFilter(Filter filter){
        kernelFilter = filter;
    }
}
