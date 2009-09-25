package workaround;

class A implements Cloneable {
    private int x;
    public A(int i) {
        x = i;
    }
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }
    public int getx() {
        return x;
    }
}

public class CloneDemo3 {
    public static void main(String args[])
      throws CloneNotSupportedException {
        A obj1 = new A(37);
        A obj2 = (A)obj1.clone();
        System.out.println(obj2.getx());
    }
}
