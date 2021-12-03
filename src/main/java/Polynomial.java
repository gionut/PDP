import java.util.Comparator;

public class Polynomial {
    Terms terms;
    Integer degree;

    public Polynomial() {
        this.degree = 0;
        this.terms = new Terms();
    }

    public Polynomial oneTermMultiplication(Polynomial a) {
        Term onlyTerm = this.terms.get(this.degree);
        if (onlyTerm.coefficient == 0)
            return new Polynomial();

        Polynomial result = new Polynomial();
        result.degree = this.degree + a.degree;

        Terms resultMap = new Terms();
        for (int degree : a.terms.keySet()) {
            Term termA = a.terms.get(degree);
            if (termA.coefficient != 0) {
                int newDegree = termA.degree + onlyTerm.degree;
                int newCoefficient = termA.coefficient * onlyTerm.coefficient;

                Term newTerm = resultMap.get(newDegree);
                if (newTerm == null)
                    resultMap.put(newDegree, new Term(newCoefficient, newDegree));
                else
                    newTerm.coefficient += newCoefficient;
            }
        }
        result.terms = resultMap;
        return result;
    }

    public void halve(Polynomial au, Polynomial al) {
        int half = this.terms.size() / 2;

        for (var key : this.terms.keySet()) {
            if (half > 0) {
                au.terms.put(key, this.terms.get(key));
                au.degree = au.degree < key ? key : au.degree;
                half--;
            } else {
                al.terms.put(key, this.terms.get(key));
                al.degree = al.degree < key ? key : al.degree;
            }
        }
    }

    public void generate(Integer degree) {
        this.degree = degree;
        this.terms = new Terms();
        for (int i = degree; i > 0; i--) {
            this.terms.put(i, new Term(i, i));
        }
    }

    public void KaratsubaHalve(Polynomial au, Polynomial al, Integer n) {
        int half = n / 2;

        for (int key : this.terms.keySet()) {
            Term term = this.terms.get(key);

            if (term.degree >= half) {
                int newKey = key - half;
                au.terms.put(newKey, new Term(term.coefficient, term.degree - half));
                au.degree = au.degree < newKey ? newKey : au.degree;
            } else {
                al.terms.put(key, new Term(term.coefficient, term.degree));
                al.degree = al.degree < key ? key : al.degree;
            }
        }
    }

    public Polynomial Sum(Polynomial p, int sign) {
        Polynomial newPolynomial = new Polynomial();

        for (Term value : this.terms.values()) {
            Term term = p.terms.get(value.degree);
            Term newTerm = new Term(value.coefficient, value.degree);
            if (term != null)
                newTerm.coefficient += sign * term.coefficient;

            newPolynomial.terms.put(value.degree, newTerm);
            newPolynomial.degree = newPolynomial.degree < value.degree ? value.degree : newPolynomial.degree;
        }

        for (Term value : p.terms.values()) {
            Term term = newPolynomial.terms.get(value.degree);
            if (term == null) {
                newPolynomial.terms.put(value.degree, new Term(value.coefficient, value.degree));
                newPolynomial.degree = newPolynomial.degree < value.degree ? value.degree : newPolynomial.degree;
            }
        }
        return newPolynomial;
    }

    public Polynomial RankUp(Integer n) {
        Polynomial p = new Polynomial();

        for (Term value : this.terms.values()) {
            int newDegree = value.degree + n;
            p.terms.put(newDegree, new Term(value.coefficient, newDegree));
        }
        p.degree += n;
        return p;
    }

    @Override
    public String toString() {
        if (terms.keySet().size() == 0)
            return "0";
        StringBuilder sb = new StringBuilder();
        terms.keySet().stream().sorted(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        }).forEach(term -> sb.append(terms.get(term)).append("+"));
        sb.replace(sb.length() - 1, sb.length(), "");
        return sb.toString();
    }
}
