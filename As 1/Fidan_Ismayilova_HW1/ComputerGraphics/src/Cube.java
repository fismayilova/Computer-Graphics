import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import java.io.IOException;

import java.nio.FloatBuffer;
import java.util.Random;
import java.util.Scanner;

import java.util.Vector;

import javax.swing.JFrame;

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

import com.jogamp.common.nio.Buffers;

public class Cube extends JFrame implements GLEventListener, MouseListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GLCanvas myCanvas;
	private int rendering_program;
	private static final int MAX = 10;
	private int vao[] = new int[1];
	private int vbo[] = new int[MAX];
	private float cameraX, cameraY, cameraZ;
	private float cubeLocX, cubeLocY, cubeLocZ;
	private Matrix4 pMat = new Matrix4();
	private double t;
	private int[] nums = new int[MAX];
	private int[] ys = new int[MAX];
	static Random rand = new Random();
	private int[] mins = new int[MAX];
	private int[] xAxis = new int[MAX];
	private int[] yAxis = new int[MAX];
	private int[] zAxis = new int[MAX];

	public Cube() {
		setTitle("Chapter4 - program1");
		setSize(2000, 2000);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		myCanvas.addMouseListener(this);
		myCanvas.addMouseMotionListener(this);
		this.add(myCanvas);
		FPSAnimator animator = new FPSAnimator(myCanvas, 100);
		animator.start();
		setVisible(true);
	}

	public void init(GLAutoDrawable drawable) {
		// GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		setupVertices();
		cameraX = 2.0f;
		cameraY = 0.0f;
		cameraZ = 10.0f;
		cubeLocX = 0.0f;
		cubeLocY = -2.0f;
		cubeLocZ = 0.0f;
		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.makePerspective(40.0f, aspect, 0.1f, 1000.0f);
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(rendering_program);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		Matrix4 vMat = new Matrix4();
		vMat.translate(-cameraX, -cameraY, -cameraZ);
		t += 0.1;
		for (int i = 0; i < vbo.length; i++) {
			Matrix4 mMat = new Matrix4();
			mMat.translate(cubeLocX, cubeLocY, cubeLocZ);
			mMat.translate(i * nums[i] * mins[i], i * ys[i] * mins[i], 0);
			mMat.rotate((float) t / 10.0f, (float) xAxis[i], (float) yAxis[i], (float) zAxis[i]);
			Matrix4 mvMat = new Matrix4();
			mvMat.multMatrix(vMat);
			mvMat.multMatrix(mMat);
			int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
			int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
			gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getMatrix(), 0);
			gl.glUniformMatrix4fv(mv_loc, 1, false, mvMat.getMatrix(), 0);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[i]);
			gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LEQUAL);
			gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);
		}
	}

	private void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		float[] vertex_positions = { -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
				1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
				-1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f,
				-1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
				-1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
				-1.0f };
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		for (int i = 0; i < vbo.length; i++) {
			nums[i] = rand.nextInt(8);
			ys[i] = -rand.nextInt(8);
			if (nums[i] % 2 == 0) {
				mins[i] = -1;
			} else {
				mins[i] = 1;
			}
			xAxis[i] = rand.nextInt(2);
			yAxis[i] = rand.nextInt(2);
			zAxis[i] = rand.nextInt(2);
			if (xAxis[i] == 0 && yAxis[i] == 0 && zAxis[i] == 0) {
				xAxis[i] = 1;
			}
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[i]);
			FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertex_positions);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL.GL_STATIC_DRAW);
		}
	}

	private int createShaderProgram() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		String vshaderSource[] = readShaderSource("src/MyShader.glsl");
		String fshaderSource[] = readShaderSource("src/FShader.glsl");
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

		new Cube();

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
		for (int j = 0; j < vbo.length; j++) {
			nums[j] = rand.nextInt(8);
			ys[j] = -rand.nextInt(8);
			if (nums[j] % 2 == 0) {
				mins[j] = -1;
			} else {
				mins[j] = 1;
			}
			xAxis[j] = rand.nextInt(2);
			yAxis[j] = rand.nextInt(2);
			zAxis[j] = rand.nextInt(2);
			if (xAxis[j] == 0 && yAxis[j] == 0 && zAxis[j] == 0) {
				xAxis[j] = 1;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}