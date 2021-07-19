import java.util.*;

public class HashListAutocomplete implements Autocompletor {
    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {

        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }

        if (terms.length != weights.length) {
            throw new IllegalArgumentException("terms and weights are not the same length");
        }
        initialize(terms,weights);
    }

    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<String, List<Term>>();
        for (int i = 0; i < terms.length; i++) {
            Term term = new Term(terms[i], weights[i]);
            int numOfKeys = Math.min(MAX_PREFIX, terms[i].length());
            for (int j = 0; j <= numOfKeys; j++) {
                String key = terms[i].substring(0, j);
                myMap.putIfAbsent(key, new ArrayList<Term>());
                myMap.get(key).add(term);
            }
        }
        for (String key: myMap.keySet()) {
            List<Term> valueList = (myMap.get(key));
            Collections.sort(valueList,Comparator.comparing(Term::getWeight).reversed());
        }
    }

    public List<Term> topMatches(String prefix, int k) {
        if (prefix == null) {
            throw new NullPointerException("Null pointer");
        }

        if (!myMap.containsKey(prefix) || k==0) {
            return new ArrayList<>();
        }
        if (k < 0)
            throw new IllegalArgumentException("Illegal value of k:" + k);

        if (prefix.length() > MAX_PREFIX) {
            prefix = prefix.substring(0, MAX_PREFIX);
        }
        List<Term> matches = myMap.get(prefix);
        if (matches == null) {
            return new ArrayList<Term>();
        }
        List<Term> list = matches.subList(0, Math.min(k, matches.size()));
        return list;
    }

    public int sizeInBytes() {
        if (mySize == 0) {

            for (String key: myMap.keySet()) {
                List<Term> valueList = (myMap.get(key));
                mySize += BYTES_PER_CHAR * key.length();
                for (Term term: valueList) {
                    mySize+=BYTES_PER_CHAR*term.getWord().length() + BYTES_PER_DOUBLE;
                }
            }
        }
        return mySize;
    }



}
