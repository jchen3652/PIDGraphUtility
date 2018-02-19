package oldpidtuner;

public class Util {
	public static double[] toRealDoubleArray(Double[] args) {
		double[] array = new double[args.length];
		for (int i = 0; i < args.length; i++) {
			array[i] = args[i];
		}
		return array;
	}
}
