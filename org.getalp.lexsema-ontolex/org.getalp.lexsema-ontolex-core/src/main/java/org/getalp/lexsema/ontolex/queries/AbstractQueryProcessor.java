package org.getalp.lexsema.ontolex.queries;


import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.expr.Expr;
import org.getalp.lexsema.ontolex.Graph;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Common methods for {@code QueryProcessor} implementations.
 *
 * @param <T> The type of {@code QueryProcessor} implemented
 */
public abstract class AbstractQueryProcessor<T> implements QueryProcessor<T> {
    private final OntologyModel model;
    private final Graph graph;
    private ARQQuery query;
    private ResultSet results;
    private final Collection<Triple> triples = new ArrayList<>();
    private final Collection<Triple> optionalTriples = new ArrayList<>();
    private final Collection<String> resultVars = new ArrayList<>();
    private final Collection<Expr> filters = new ArrayList<>();

    protected AbstractQueryProcessor(final Graph graph) {
        this.graph = graph;
        model = graph.getModel();
    }

    @Override
    public void runQuery() {
        results = query.runQuery();
    }


    protected Node getNode(final String uri) {
        return model.getNode(uri);
    }

    protected void initialize() {
        defineQuery();
        query.initialize(graph, triples, optionalTriples, resultVars, filters);
    }

    protected void addTriple(final Node first, final Node second, final Node third) {
        triples.add(Triple.create(first, second, third));
    }

    protected void addOptionalTriple(final Node first, final Node second, final Node third) {
        optionalTriples.add(Triple.create(first, second, third));
    }

    protected void addFilter(final Expr e) {
        filters.add(e);
    }

    protected void addResultVar(final String variable) {
        resultVars.add(variable);
    }

    protected boolean hasNextResult() {
        return results.hasNext();
    }

    protected QuerySolution nextSolution() {
        return results.nextSolution();
    }

    protected abstract void defineQuery();

    @Override
    public ARQQuery getQuery() {
        return query;
    }

    protected void setQuery(final ARQQuery query) {
        this.query = query;
    }

}
