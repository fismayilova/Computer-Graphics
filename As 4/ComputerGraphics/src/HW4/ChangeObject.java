package HW4;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class ChangeObject extends JFrame implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GLCanvas myCanvas;
	private int rendering_program;
	private static final int MAX = 7;
	private int vao[] = new int[1];
	private int vbo[] = new int[MAX];
	private float cameraX, cameraY, cameraZ;
	private Matrix4 pMat = new Matrix4();
	// private double t;
	static Random rand = new Random();
	private int j = 1;
	private float pyrLocX;
	private float pyrLocY;
	private float pyrLocZ;
	private float dotP;

	private Vector3 rot = new Vector3(1, 1, 1);
	private Vector3 rotReleased = new Vector3();
	private Vector3 cross;
	private Vector3 myVec = new Vector3();
	private Vector3 origin = new Vector3(0, 0, 0);
	private Vector3 toOrig = new Vector3();

	private float size = 45.0f;
	Vector3 camera = new Vector3(0, 0, 5);
	Vector3 light = new Vector3(0, 2, 0);

	Vector3 light_ambient = new Vector3(1, 0, 1);
	Vector3 light_diffuse = new Vector3(0, 1, 1);
	Vector3 light_specular = new Vector3(1, 1, 1);

	Vector3 mat_ambient = new Vector3(1.0f, 0.2f, 0.5f);
	Vector3 mat_diffuse = new Vector3(0.8f, 0.6f, 0.6f);
	Vector3 mat_specular = new Vector3(1.0f, 1.0f, 1.0f);

	Texture joglBrickTexture;
	private int brickTexture;
	float shininess = 50f;

	private int pointX;

	private int pointY;

	private int newPointX;

	private int newPointY;

	private int CUBEvertices;

	private int PYRAvertices;

	private Texture joglPyraTexture;

	private int pyraTexture;

	private Sphere mySphere;

	private Texture joglSphereTexture;

	private int sphereTexture;

	public ChangeObject() {
		setTitle("ChangeObject");
		setSize(3000, 2000);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		myCanvas.addMouseListener(this);
		myCanvas.addKeyListener(this);
		myCanvas.addMouseMotionListener(this);
		this.add(myCanvas);
		FPSAnimator animator = new FPSAnimator(myCanvas, 100);
		animator.start();
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				animator.stop();
				dispose();
				System.exit(0);
			}
		});
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(rendering_program);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		Matrix4 vMat = new Matrix4();
		vMat.translate(-cameraX, -cameraY, -cameraZ);
		if (Math.abs(cross.getX()) < 1f) {
			dotP -= 0.05;
		} else {
			dotP += 0.05;
		}
		Matrix4 mMat = new Matrix4();
		mMat.translate(pyrLocX, pyrLocY, pyrLocZ);
		mMat.rotate(dotP / 10f, (float) Math.abs((cross.getX())), (float) Math.abs((cross.getY())),
				(float) Math.abs((cross.getZ())));
		Matrix4 mvMat = new Matrix4();
		mvMat.multMatrix(vMat);
		mvMat.multMatrix(mMat);

		int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvMat.getMatrix(), 0);
		int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
		gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getMatrix(), 0);

		int locVert = gl.glGetAttribLocation(rendering_program, "pos");
		int locTex = gl.glGetAttribLocation(rendering_program, "texCoord");

		if (j == 0) {
			joglBrickTexture = loadTexture("src/HW4/cube.bmp");
			brickTexture = joglBrickTexture.getTextureObject();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			gl.glEnableVertexAttribArray(locVert);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
			gl.glVertexAttribPointer(locVert, 3, GL4.GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(locTex);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[2]);
			gl.glVertexAttribPointer(locTex, 3, GL4.GL_FLOAT, false, 0, 0);
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, brickTexture);
			gl.glEnable(GL4.GL_DEPTH_TEST);
			gl.glDepthFunc(GL4.GL_LEQUAL);
			gl.glDrawArrays(GL4.GL_TRIANGLES, 0, CUBEvertices / 3);
		} else if (j == 1) {
			joglPyraTexture = loadTexture("src/HW4/pyramid.bmp");
			pyraTexture = joglPyraTexture.getTextureObject();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			gl.glEnableVertexAttribArray(locVert);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
			gl.glVertexAttribPointer(locVert, 3, GL4.GL_FLOAT, false, 0, 0);

			gl.glEnableVertexAttribArray(locTex);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[3]);
			gl.glVertexAttribPointer(locTex, 3, GL4.GL_FLOAT, false, 0, 0);
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, pyraTexture);
			gl.glEnable(GL4.GL_DEPTH_TEST);
			gl.glDepthFunc(GL4.GL_LEQUAL);
			gl.glDrawArrays(GL4.GL_TRIANGLES, 0, PYRAvertices / 3);
		} else {
			joglSphereTexture = loadTexture("src/HW4/sphere.bmp");
			sphereTexture = joglSphereTexture.getTextureObject();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
			gl.glEnableVertexAttribArray(locVert);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[4]);
			gl.glVertexAttribPointer(locVert, 3, GL4.GL_FLOAT, false, 0, 0);

			gl.glEnableVertexAttribArray(locTex);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[5]);
			gl.glVertexAttribPointer(locTex, 3, GL4.GL_FLOAT, false, 0, 0);
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, sphereTexture);
			gl.glEnable(GL4.GL_DEPTH_TEST);
			gl.glDepthFunc(GL4.GL_LEQUAL);
			int numVerts = mySphere.getIndices().length;
			gl.glDrawArrays(GL.GL_TRIANGLES, 0, numVerts);
		}
	}

	public Texture loadTexture(String textureFileName) {
		Texture tex = null;
		try {
			tex = TextureIO.newTexture(new File(textureFileName), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tex;
	}

	private void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		float[] vertex_positions_cube = { -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
				-1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
				-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
				1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f,
				-1.0f, 1.0f, -1.0f };
		float[] cube_texture_coordinates = vertex_positions_cube;
		CUBEvertices = vertex_positions_cube.length;
		float[] vertex_pyramid = { -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f, -1.0f,
				-1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
				1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, };
		PYRAvertices = vertex_pyramid.length;
		float[] pyr_texture_coordinates = vertex_pyramid;
		Vector3[] vertices = mySphere.getVertices();
		int[] indices = mySphere.getIndices();
		float[] pvalues = new float[indices.length * 3];
		float[] tvalues = new float[indices.length * 2];
		float[] nvalues = new float[indices.length * 3];
		for (int i = 0; i < indices.length; i++) {
			pvalues[i * 3] = (float) (vertices[indices[i]]).getX();
			pvalues[i * 3 + 1] = (float) (vertices[indices[i]]).getY();
			pvalues[i * 3 + 2] = (float) (vertices[indices[i]]).getZ();
			tvalues[i * 2] = (float) (vertices[indices[i]]).getS();
			nvalues[i * 3] = (float) (vertices[indices[i]]).getNormalX();
			nvalues[i * 3 + 1] = (float) (vertices[indices[i]]).getNormalY();
			nvalues[i * 3 + 2] = (float) (vertices[indices[i]]).getNormalZ();
		}
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(vertex_positions_cube);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeBuf.limit() * 4, cubeBuf, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(vertex_pyramid);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, pyrBuf.limit() * 4, pyrBuf, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer cTexBuf = Buffers.newDirectFloatBuffer(cube_texture_coordinates);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, cTexBuf.limit() * 4, cTexBuf, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer pTexBuf = Buffers.newDirectFloatBuffer(pyr_texture_coordinates);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, pTexBuf.limit() * 4, pTexBuf, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, norBuf.limit() * 4, norBuf, GL.GL_STATIC_DRAW);

	}

	@Override
	public void init(GLAutoDrawable arg0) {
		rendering_program = createShaderProgram();
		mySphere = new Sphere(48);
		cross = new Vector3(1, 1, 1);
		setupVertices();
		cameraX = 0.0f;
		cameraY = 2.0f;
		cameraZ = 10.0f;
		pyrLocX = 50.0f;
		pyrLocY = 50.0f;
		pyrLocZ = -10.0f;
		pMat.loadIdentity();
		pyrLocX = 0.0f;
		pyrLocY = 2.0f;
		pyrLocZ = -8.0f;
		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.makePerspective(45.0f, aspect, 0.1f, 1000.0f);
	}

	private int createShaderProgram() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		String vshaderSource[] = readShaderSource("src/HW4/VshaderPyra.glsl");
		String fshaderSource[] = readShaderSource("src//HW4/FshaderPyra.glsl");
		int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);
		printShaderLog(vShader);
		int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);
		printShaderLog(fShader);
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfprogram;
	}

	private String[] readShaderSource(String filename) {

		Vector<String> lines = new Vector<String>();

		Scanner sc;

		try {

			sc = new Scanner(new File(filename));

		} catch (IOException e) {

			System.err.println("IOException reading file: " + e);

			return null;

		}

		while (sc.hasNext()) {

			lines.addElement(sc.nextLine());

		}
		sc.close();
		String[] program = new String[lines.size()];
		for (int i = 0; i < lines.size(); i++) {
			program[i] = (String) lines.elementAt(i) + '\n';
			System.out.print(program[i]);
		}
		return program;

	}

	private void printShaderLog(int shader) {

		GL2 gl = (GL2) GLContext.getCurrentGL();

		int[] len = new int[1];

		int[] chWrittn = new int[1];

		byte[] log = null;

		// determine the length of the shader compilation log

		gl.glGetShaderiv(shader, GL3.GL_INFO_LOG_LENGTH, len, 0);

		if (len[0] > 0) {

			log = new byte[len[0]];

			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);

			System.out.println("Shader Info Log: ");

			for (int i = 0; i < log.length; i++) {

				System.out.print((char) log[i]);

			}

		}

	}

	void printProgramLog(int prog) {

		GL2 gl = (GL2) GLContext.getCurrentGL();

		int[] len = new int[1];

		int[] chWrittn = new int[1];

		byte[] log = null;

		// determine the length of the program linking log

		gl.glGetProgramiv(prog, GL3.GL_INFO_LOG_LENGTH, len, 0);

		if (len[0] > 0) {

			log = new byte[len[0]];

			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);

			System.out.println("Program Info Log: ");

			for (int i = 0; i < log.length; i++) {

				System.out.print((char) log[i]);

			}

		}

	}

	public static void main(String[] args) {

		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		new ChangeObject();

	}

	@Override

	public void dispose(GLAutoDrawable arg0) {

		// TODO Auto-generated method stub

	}

	@Override

	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent a) {
		pointX = a.getX();
		pointY = a.getY();
		rot.setX((float) Math.abs(a.getX() / (myCanvas.getWidth() / 0.5)));
		rot.setY((float) Math.abs(a.getY() / (myCanvas.getHeight() / 0.5)));
		rot.setZ((float) Math.sqrt(1 - (Math.pow(rot.getX(), 2)) - Math.pow(rot.getY(), 2)));
	}

	@Override
	public void mouseReleased(MouseEvent a) {
		newPointX = a.getX();
		newPointY = a.getY();
		if (newPointX == pointX && newPointY == pointY) {

		} else {
			rotReleased.setX((float) (a.getX() / (myCanvas.getWidth() / 0.5)));
			rotReleased.setY((float) (a.getY() / (myCanvas.getHeight() / 0.5)));
			rotReleased
					.setZ((float) Math.sqrt(1 - (Math.pow(rotReleased.getX(), 2)) - Math.pow(rotReleased.getY(), 2)));
			myVec = myVec.setCoord(rot, rotReleased);
			toOrig = toOrig.setCoord(rotReleased, origin);
			cross = myVec.crossProd(toOrig);
			myVec.dotProd(toOrig);
			dotP -= (float) Math.acos((myVec.dotProd(toOrig)) / (myVec.getLength() * toOrig.getLength()));
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_C:
			j = 0;
			break;
		case KeyEvent.VK_S:
			j = 2;
			break;
		case KeyEvent.VK_P:
			j = 1;
			break;
		case KeyEvent.VK_O:
			pMat.loadIdentity();
			pyrLocX = 50.0f;
			pyrLocY = 50.0f;
			pyrLocZ = -10.0f;
			pMat.makeOrtho(0.1f, 100f, 0.1f, 100f, 0.1f, 500f);
			break;
		case KeyEvent.VK_F:
			pMat.loadIdentity();
			pyrLocX = 0.0f;
			pyrLocY = 2.0f;
			pyrLocZ = -8.0f;
			float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
			pMat.makePerspective(size, aspect, 0.1f, 1000.0f);
			break;
		case KeyEvent.VK_ADD:
			pyrLocZ++;
			break;
		case KeyEvent.VK_SUBTRACT:
			pyrLocZ--;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
