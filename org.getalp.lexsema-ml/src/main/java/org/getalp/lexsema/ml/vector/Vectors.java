package org.getalp.lexsema.ml.vector;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vectors {

    private static DoubleMatrix1D perm;

    public static DoubleMatrix1D permutation(DoubleMatrix1D input) {
        List<PermutationItem> perm = new ArrayList<PermutationItem>();
        for (int i = 0; i < input.size(); i++) {
            perm.add(new PermutationItem(i, input.getQuick(i)));
        }
        Collections.sort(perm);
        DoubleMatrix1D output = new DenseDoubleMatrix1D(perm.size());
        for (int i = 0; i < perm.size(); i++) {
            output.setQuick(i, perm.get(i).origIndex);
        }
        return output;
    }

    public static synchronized DoubleMatrix1D getDefaultPermutation(DoubleMatrix1D input) {
        if (perm == null || input.size() >= perm.size()) {
            perm = new DenseDoubleMatrix1D((int) input.size());
            for (int i = 0; i < perm.size(); i++) {
                perm.setQuick(i, i);
            }
        }
        return perm.viewPart(0, (int) input.size());
    }

    public static DoubleMatrix1D getIdentity(int size) {
        DoubleMatrix1D perm = new DenseDoubleMatrix1D(size);
        for (int i = 0; i < perm.size(); i++) {
            perm.setQuick(i, 1);
        }
        return perm;
    }

    public static INDArray contextualProduct(INDArray vectorA, INDArray vectorB){
        return null;
    }

    public static INDArray contextualSum(INDArray vectorA, INDArray vectorB){
        return null;
    }

    public static INDArray weakContextualization(INDArray vectorA, INDArray vectorB){
        return null;
    }

}
