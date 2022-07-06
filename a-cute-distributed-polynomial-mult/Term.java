public class Term {
    Integer coefficient;
    Integer degree;

    public Term(Integer coefficient, Integer degree) {
        this.coefficient = coefficient;
        this.degree = degree;
    }

    public String toString() {
        return coefficient + "x^" + degree;
    }
}
