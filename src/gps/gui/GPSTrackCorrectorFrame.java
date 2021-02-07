package gps.gui;
import gps.common.CommonData;
import gps.track.GPSTrack;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GPSTrackCorrectorFrame extends JFrame
{
	private static final long serialVersionUID = 6526653766576132152L;

	private JComboBox<String> cbDeviceName;
	private JTextField txtSrcFileName;
	private JButton btnChooseSrcFile;
	private JTextField txtTrackName;
	
	private JTextField txtOutFileName;
	private JButton btnChooseOutFile;
	private JSpinner spinOutDateFrom;
	private JTextField txtMaxOutDeviation;
	private JButton btnGenerate;
	
	
	// main
	public static void main(String[] args)
	{
		EventQueue.invokeLater(() -> 
			{
				GPSTrackCorrectorFrame frame = new GPSTrackCorrectorFrame();
				frame.setVisible(true);	
			});
	}
		
	
	// constructor
	public GPSTrackCorrectorFrame() 
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700, 360);
		setTitle("Корректор GPS-трека");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		// set "nimbus" look and feel
		CommonData.selectLookAndFeel();
		// init controls
		initControls();
		// localize file chooser component to russian
		CommonData.localizeFileChooserToRussian();
	}
	
	// initialize controls
	private void initControls()
	{
		// source data
		JPanel pnlSourceData = createSourceDataPanel();
		// output data
		JPanel pnlOutputData = createOutDataPanel();
		// button
		btnGenerate = new JButton("Создать");
		btnGenerate.addActionListener(e -> generateTrack());
		Box boxBottom = Box.createHorizontalBox();
		boxBottom.add(Box.createHorizontalGlue());
		boxBottom.add(btnGenerate);
		boxBottom.add(Box.createRigidArea(new Dimension(15, 40)));
		// add all to frame
		add(pnlSourceData);
		add(pnlOutputData);
		add(boxBottom);
	}
	
	
	// create source data panel with controls
	private JPanel createSourceDataPanel()
	{
		// panel with title
		JPanel pnlSourceData = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		pnlSourceData.setBorder(new TitledBorder("Исходные данные"));
		// create controls
		cbDeviceName = new JComboBox<String>(CommonData.getDeviceNames());
		txtSrcFileName = new JTextField(CommonData.getCurrentDir() + "rev1.gpx");
		btnChooseSrcFile = new JButton();
		txtTrackName = new JTextField("Трек");
		// add controls
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weightx = 0.1;
		pnlSourceData.add(new JLabel("Модель навигатора"), gbc);
		pnlSourceData.add(new JLabel("Имя исходного файла (.gpx)"), gbc);
		pnlSourceData.add(new JLabel("Наименование трека"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 2;
		pnlSourceData.add(cbDeviceName, gbc);
		pnlSourceData.add(makeChooseFileBox(txtSrcFileName, btnChooseSrcFile), gbc);
		pnlSourceData.add(txtTrackName, gbc);	
		return pnlSourceData;
	}
		
	// create output data panel
	private JPanel createOutDataPanel()
	{
		// initialize calendar
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// panel with title
		JPanel pnlOutData = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		pnlOutData.setBorder(new TitledBorder("Выходные данные"));
		// create controls
		// revision 1 file name
		txtOutFileName = new JTextField(CommonData.getCurrentDir() + "rev1_new.gpx");
		btnChooseOutFile = new JButton();
		// revision 1 date and time from
		spinOutDateFrom = new JSpinner(new SpinnerDateModel());
		spinOutDateFrom.setEditor(new JSpinner.DateEditor(spinOutDateFrom, CommonData.DATE_TIME_FORMAT));
		spinOutDateFrom.setValue(cal.getTime());		
		// max coordinate deviation
		txtMaxOutDeviation = new JTextField(CommonData.REV2_DEVIATION);
		// add controls
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weightx = 0.1;
		pnlOutData.add(new JLabel("Имя выходного файла (.gpx)"), gbc);
		pnlOutData.add(new JLabel("Дата и время начала"), gbc);
		pnlOutData.add(new JLabel("Отклонение координат"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 2;
		pnlOutData.add(makeChooseFileBox(txtOutFileName, btnChooseOutFile), gbc);
		pnlOutData.add(spinOutDateFrom, gbc);
		pnlOutData.add(txtMaxOutDeviation, gbc);
		return pnlOutData;
	}
	
	
	// make horizontal box with JTextField and JButton
	private Box makeChooseFileBox(JTextField textField, JButton button)
	{
		Box box = Box.createHorizontalBox();
		box.add(textField);
		box.add(button);
		try {
			Image img = ImageIO.read(getClass().getResource("/images/folder.png"));
			button.setIcon(new ImageIcon(img));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		button.addActionListener(e -> chooseFile(textField));
		return box;
	}
	
	
	// choose file 
	private void chooseFile(JTextField textField)
	{			
		String fileName = "";
		// initialize file chooser
		JFileChooser fc = new JFileChooser();
		// set current directory to chooser
		fc.setCurrentDirectory(new File(CommonData.getCurrentDir()));
		// set default file name if not select source
		fc.setSelectedFile(new File(CommonData.getCurrentDir() + File.separator + textField.getText()));			
		// set filter
		fc.setFileFilter(new FileNameExtensionFilter("GPS eXchange", "gpx"));
		fc.setAcceptAllFileFilterUsed(false);
		// show open dialog for source file
		// and save dialog for output files
		int retVal = (textField == txtSrcFileName) ? fc.showOpenDialog(textField) : fc.showSaveDialog(textField);			
		if (retVal == JFileChooser.APPROVE_OPTION)
		{
			// check suffix .gpx
			fileName = fc.getSelectedFile().getAbsolutePath();
			if (!fileName.endsWith(".gpx"))
				fileName += ".gpx";
			// set text
			textField.setText(fileName);
		}
	}
	
	
	// check input file and output file directories existing
	private boolean checkAllFilePaths()
	{
		boolean result = true;
		String errMsg = "";
		if (!CommonData.checkSourceFileExists(txtSrcFileName.getText()))
		{
			result = false;
			errMsg += String.format(CommonData.MSG_SRC_FILE_NOT_EXISTS, txtSrcFileName.getText());
		}
		if (!CommonData.checkOutPathCorrect(txtOutFileName.getText()))
		{
			result = false;
			errMsg += String.format(CommonData.MSG_OUT_PATH_NOT_CORRECT, "данных");
		}
		if (!result)
		{
			JOptionPane.showMessageDialog(this, errMsg, "Ошибка ввода данных", JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}
	
	
	// generate track
	private void generateTrack()
	{
		// check all file paths
		if (!checkAllFilePaths())
			return;
		//
		// load .gpx-file and create track
		GPSTrack track = new GPSTrack(txtSrcFileName.getText(), 
									  (String)cbDeviceName.getSelectedItem(),
									  txtTrackName.getText());
		// shift points date
		track.shiftTrackDate((Date)spinOutDateFrom.getValue());
		// shift points coordinates
		track.shiftCoordinates(Double.parseDouble(txtMaxOutDeviation.getText()));
		// write track to .gpx-file
		if (!track.writeGpxFile(txtOutFileName.getText()))
		{
			JOptionPane.showMessageDialog(this, "Не удалось сохранить измененный трек", "Ошибка записи", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// show success dialog
		JOptionPane.showMessageDialog(this, "Измененный трек успешно сохранен", "Информация", JOptionPane.INFORMATION_MESSAGE);
	}
}
