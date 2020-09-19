package HW4;

public class Vector3 {
	private float x, y, z, s, t;

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(float s, float t) {
		this.s = s;
		this.t = t;
	}

	public Vector3() {
		// TODO Auto-generated constructor stub
	}

	public float getX() {
		return this.x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return this.y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return this.z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public Vector3 crossProd(Vector3 vector) {
		Vector3 cross = new Vector3();
		cross.setX((this.y * vector.getZ()) - (this.z * vector.getY()));
		cross.setY((this.z * vector.getX()) - (this.x * vector.getZ()));
		cross.setZ((this.x * vector.getY()) - (this.y * vector.getX()));
		return cross;
	}

	public float dotProd(Vector3 vector) {
		float sum;
		sum = this.x * vector.getX() + this.y * vector.getY() + this.z * vector.getZ();
		return sum;
	}

	public void setLocation(float x2, float y2, float z2) {
		this.x = x2;
		this.y = y2;
		this.z = z2;
	}

	public float getNormalX() {
		return 0;
	}

	public float getNormalY() {
		return 0;
	}

	public float getNormalZ() {
		return 0;
	}

	public float getS() {
		return s;
	}

	public float getT() {
		return t;
	}

	public void setS(float f) {
		this.s = f;
	}

	public void setT(float f) {
		this.t = f;
	}

	public Vector3 setCoord(Vector3 rot, Vector3 rotReleased) {
		Vector3 myVec = new Vector3();
		myVec.setX(rotReleased.getX() - rot.getX());
		myVec.setY(rotReleased.getY() - rot.getY());
		myVec.setZ(rotReleased.getZ() - rot.getZ());
		return myVec;
	}

	public double getLength() {
		return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
	}

	public void mult(Vector3 v) {
		this.x = this.x * v.getX();
		this.y = this.y * v.getY();
		this.z = this.z * v.getZ();
	}

	public float[] getFloats() {
		return new float[] { x, y, z };
	}

	public void setNormal(float[] fs) {
		// TODO Auto-generated method stub

	}

}
