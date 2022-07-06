import java.util.HashMap;

public class Terms extends HashMap<Integer, Term> {

    public void addAll(HashMap<Integer, Term> map) {
        for (int key : map.keySet()) {
            Term t = this.get(key);
            if (t == null)
                this.put(key, map.get(key));
            else
                t.coefficient += map.get(key).coefficient;
        }
    }
}
