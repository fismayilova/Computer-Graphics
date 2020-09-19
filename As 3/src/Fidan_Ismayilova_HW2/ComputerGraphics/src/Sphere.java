package Fidan_Ismayilova_HW2.ComputerGraphics.src;

public class Sphere {
	private int numVertices, numIndices, prec;
	private int[] indices;

	private Vector3[] vertices;

	public Sphere(int p) {
		prec = p;
		initSphere();
	}

	private void initSphere() {
		numVertices = (prec + 1) * (prec + 1);
		numIndices = prec * prec * 6;
		vertices = new Vector3[numVertices];
		indices = new int[numIndices];
		for (int i = 0; i < numVertices; i++) {
			vertices[i] = new Vector3();
		}
		for (int i = 0; i <= prec; i++) {
			for (int j = 0; j <= prec; j++) {
				float y = (float) Math.cos(Math.toRadians(180 - i * 180 / prec));
				float x = -(float) Math.cos(Math.toRadians(j * 360 / prec)) * (float) Math.abs(Math.cos(Math.asin(y)));
				float z = (float) Math.sin(Math.toRadians(j * 360 / prec)) * (float) Math.abs(Math.cos(Math.asin(y)));
				vertices[i * (prec + 1) + j].setLocation(x, y, z);
				vertices[i * (prec + 1) + j].setS((float) j / prec);
				vertices[i * (prec + 1) + j].setT((float) i / prec);
				vertices[i * (prec + 1) + j].crossProd(new Vector3(vertices[i * (prec + 1) + j].getX(),
						vertices[i * (prec + 1) + j].getY(), vertices[i * (prec + 1) + j].getZ()));
			}
		}
		for (int i = 0; i < prec; i++) {
			for (int j = 0; j < prec; j++) {
				indices[6 * (i * prec + j) + 0] = i * (prec + 1) + j;
				indices[6 * (i * prec + j) + 1] = i * (prec + 1) + j + 1;
				indices[6 * (i * prec + j) + 2] = (i + 1) * (prec + 1) + j;
				indices[6 * (i * prec + j) + 3] = i * (prec + 1) + j + 1;
				indices[6 * (i * prec + j) + 4] = (i + 1) * (prec + 1) + j + 1;
				indices[6 * (i * prec + j) + 5] = (i + 1) * (prec + 1) + j;
			}
		}
	}

	public int[] getIndices() {
		return indices;
	}

	public Vector3[] getVertices() {
		return vertices;
	}

}
