package Fidan_Ismayilova_HW2.ComputerGraphics.src;

import java.awt.Point;
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

public class ChangeObject extends JFrame implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GLCanvas myCanvas;
	private int rendering_program;
	private static final int MAX = 4;
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

	float shininess = 50f;

	private int CUBEvertices;

	private int PYRAvertices;

	private int pointX;

	private int pointY;

	private int newPointX;

	private int newPointY;

	private Random rn;

	private float x;

	private float y;

	private float z;

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

		int locVert = gl.glGetAttribLocation(rendering_program, "position");
		int locNorm = gl.glGetAttribLocation(rendering_program, "normal");

		if (j % 2 == 0) {
			gl.glEnableVertexAttribArray(locVert);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
			gl.glVertexAttribPointer(locVert, 3, GL4.GL_FLOAT, false, 0, 0);

			gl.glEnableVertexAttribArray(locNorm);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[2]);
			gl.glVertexAttribPointer(locNorm, 3, GL4.GL_FLOAT, false, 0, 0);

			gl.glEnable(GL4.GL_DEPTH_TEST);
			gl.glDepthFunc(GL4.GL_LEQUAL);
			gl.glDrawArrays(GL4.GL_TRIANGLES, 0, CUBEvertices / 3);
		} else {
			gl.glEnableVertexAttribArray(locVert);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
			gl.glVertexAttribPointer(locVert, 3, GL4.GL_FLOAT, false, 0, 0);

			gl.glEnableVertexAttribArray(locNorm);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[3]);
			gl.glVertexAttribPointer(locNorm, 3, GL4.GL_FLOAT, false, 0, 0);

			gl.glEnable(GL4.GL_DEPTH_TEST);
			gl.glDepthFunc(GL4.GL_LEQUAL);
			gl.glDrawArrays(GL4.GL_TRIANGLES, 0, PYRAvertices / 3);
		}
		mat_ambient.mult(light_ambient);
		mat_diffuse.mult(light_diffuse);
		mat_specular.mult(light_specular);

		int locLight = gl.glGetUniformLocation(rendering_program, "light");
		int locAmbient = gl.glGetUniformLocation(rendering_program, "ambient");
		int locDiffuse = gl.glGetUniformLocation(rendering_program, "diffuse");
		int locSpecular = gl.glGetUniformLocation(rendering_program, "specularL");
		int locShiny = gl.glGetUniformLocation(rendering_program, "shininess");

		gl.glUniform3fv(locLight, 1, light.getFloats(), 0);
		gl.glUniform3fv(locAmbient, 1, mat_ambient.getFloats(), 0);
		gl.glUniform3fv(locDiffuse, 1, mat_diffuse.getFloats(), 0);
		gl.glUniform3fv(locSpecular, 1, mat_specular.getFloats(), 0);
		gl.glUniform1f(locShiny, shininess);
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
		CUBEvertices = vertex_positions_cube.length;
		float[] normals_CUBE = vertex_positions_cube;
		float[] vertex_pyramid = { -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f, -1.0f,
				-1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
				1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, };
		PYRAvertices = vertex_pyramid.length;
		float[] normals_PYRA = vertex_pyramid;
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(vertex_positions_cube);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeBuf.limit() * 4, cubeBuf, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(vertex_pyramid);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, pyrBuf.limit() * 4, pyrBuf, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer normCube = Buffers.newDirectFloatBuffer(normals_CUBE);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, normCube.limit() * 4, normCube, GL4.GL_STATIC_DRAW);
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer normPyra = Buffers.newDirectFloatBuffer(normals_PYRA);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, normPyra.limit() * 4, normPyra, GL4.GL_STATIC_DRAW);
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		rendering_program = createShaderProgram();
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
		String vshaderSource[] = readShaderSource("src/VVVVSHADER.glsl");
		String fshaderSource[] = readShaderSource("src/FFFFSHADER.glsl");
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
		if (light_diffuse.getX() == 0 && light_diffuse.getY() == 0 && light_diffuse.getZ() == 0
				&& light_specular.getX() == 0 && light_specular.getY() == 0 && light_specular.getZ() == 0) {
			light_diffuse.setX(0);
			light_diffuse.setY(1);
			light_diffuse.setZ(1);
			light_specular.setX(1);
			light_specular.setY(1);
			light_specular.setZ(1);
			mat_ambient.setX(1.0f);
			mat_ambient.setY(0.2f);
			mat_ambient.setZ(0.5f);
			mat_diffuse.setX(0.8f);
			mat_diffuse.setY(0.6f);
			mat_diffuse.setZ(0.6f);
			mat_specular.setX(1.0f);
			mat_specular.setY(1.0f);
			mat_specular.setZ(1.0f);
		} else {
			light_diffuse.setX(0);
			light_diffuse.setY(0);
			light_diffuse.setZ(0);
			light_specular.setX(0);
			light_specular.setY(0);
			light_specular.setZ(0);
		}
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
