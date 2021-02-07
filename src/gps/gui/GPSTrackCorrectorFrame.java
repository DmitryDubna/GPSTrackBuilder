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
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GPSTrackCorrectorFrame extends JFrame
{
	private static final long serialVersionUID = 6526653766576132152L;

	private JComboBox<String> cbDeviceName;
	private JTextField txtSrcFileName;
	private JButton btnChooseSrcFile;
	private JTextField txtTrackName;
	
	private JTextField txtFileNameRev1;
	private JButton btnChooseFileRev1;
	private JSpinner spinDateFromRev1;
	private JSpinner spinDateToRev1;
	private JTextField txtMaxDeviationRev1;
	private JSpinner spinPointsPerSectionRev1;
	
	private JTextField txtFileNameRev2;
	private JButton btnChooseFileRev2;
	private JSpinner spinDateFromRev2;
	private JSpinner spinDateToRev2;
	private JTextField txtMaxDeviationRev2;
	private JSpinner spinPointsPerSectionRev2;
	
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
		// revision 1 data
		JPanel pnlRev1Data = createRev1DataPanel();
		// revision 2 data
		JPanel pnlRev2Data = createRev2DataPanel();
		// button
		btnGenerate = new JButton("Создать");
		btnGenerate.addActionListener(e -> generateAllTracks());
		Box boxBottom = Box.createHorizontalBox();
		boxBottom.add(Box.createHorizontalGlue());
		boxBottom.add(btnGenerate);
		boxBottom.add(Box.createRigidArea(new Dimension(15, 40)));
		// add all to frame
		add(pnlSourceData);
		add(pnlRev1Data);
		add(pnlRev2Data);
		add(boxBottom);
		pack();
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
	private JPanel createRev1DataPanel()
	{
		// initialize calendar
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// panel with title
		JPanel pnlOutData = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		pnlOutData.setBorder(new TitledBorder("Данные затирки"));
		// create controls
		// revision 1 file name
		txtFileNameRev1 = new JTextField(CommonData.getCurrentDir() + "rev1_new.gpx");
		btnChooseFileRev1 = new JButton();
		// revision 1 date and time from
		spinDateFromRev1 = new JSpinner(new SpinnerDateModel());
		spinDateFromRev1.setEditor(new JSpinner.DateEditor(spinDateFromRev1, CommonData.DATE_TIME_FORMAT));
		spinDateFromRev1.setValue(cal.getTime());		
		// revision 1 date and time to		
		spinDateToRev1 = new JSpinner(new SpinnerDateModel());
		spinDateToRev1.setEditor(new JSpinner.DateEditor(spinDateToRev1, CommonData.DATE_TIME_FORMAT));
		cal.add(Calendar.HOUR, 3);
		spinDateToRev1.setValue(cal.getTime());
		// max coordinate deviation
		txtMaxDeviationRev1 = new JTextField(CommonData.REV2_DEVIATION);
		// max count of extended points per section
		spinPointsPerSectionRev1 = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
		// add controls
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weightx = 0.1;
		pnlOutData.add(new JLabel("Имя выходного файла (.gpx)"), gbc);
		pnlOutData.add(new JLabel("Дата и время начала"), gbc);
		pnlOutData.add(new JLabel("Дата и время окончания"), gbc);
		pnlOutData.add(new JLabel("Отклонение координат"), gbc);
		pnlOutData.add(new JLabel("Макс. число промежуточных точек"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 2;
		pnlOutData.add(makeChooseFileBox(txtFileNameRev1, btnChooseFileRev1), gbc);
		pnlOutData.add(spinDateFromRev1, gbc);
		pnlOutData.add(spinDateToRev1, gbc);
		pnlOutData.add(txtMaxDeviationRev1, gbc);
		pnlOutData.add(spinPointsPerSectionRev1, gbc);
		return pnlOutData;
	}
	
	
	// create output data panel
	private JPanel createRev2DataPanel()
	{
		// initialize calendar
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// panel with title
		JPanel pnlOutData = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		pnlOutData.setBorder(new TitledBorder("Данные учета"));
		// create controls
		// revision 2 file name
		txtFileNameRev2 = new JTextField(CommonData.getCurrentDir() + "rev2_new.gpx");
		btnChooseFileRev2 = new JButton();
		// revision 2 date and time from
		spinDateFromRev2 = new JSpinner(new SpinnerDateModel());
		spinDateFromRev2.setEditor(new JSpinner.DateEditor(spinDateFromRev2, CommonData.DATE_TIME_FORMAT));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.HOUR, 1);
		cal.add(Calendar.MINUTE, 30);
		cal.add(Calendar.SECOND, 15);
		spinDateFromRev2.setValue(cal.getTime());		
		// revision 2 date and time to		
		spinDateToRev2 = new JSpinner(new SpinnerDateModel());
		spinDateToRev2.setEditor(new JSpinner.DateEditor(spinDateToRev2, CommonData.DATE_TIME_FORMAT));
		cal.add(Calendar.HOUR, 3);
		spinDateToRev2.setValue(cal.getTime());
		// max coordinate deviation
		txtMaxDeviationRev2 = new JTextField(CommonData.REV2_DEVIATION);
		// max count of extended points per section
		spinPointsPerSectionRev2 = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
		// add controls
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weightx = 0.1;
		pnlOutData.add(new JLabel("Имя выходного файла (.gpx)"), gbc);
		pnlOutData.add(new JLabel("Дата и время начала"), gbc);
		pnlOutData.add(new JLabel("Дата и время окончания"), gbc);
		pnlOutData.add(new JLabel("Отклонение координат"), gbc);
		pnlOutData.add(new JLabel("Макс. число промежуточных точек"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 2;
		pnlOutData.add(makeChooseFileBox(txtFileNameRev2, btnChooseFileRev2), gbc);
		pnlOutData.add(spinDateFromRev2, gbc);
		pnlOutData.add(spinDateToRev2, gbc);
		pnlOutData.add(txtMaxDeviationRev2, gbc);
		pnlOutData.add(spinPointsPerSectionRev2, gbc);
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
		if (!CommonData.checkOutPathCorrect(txtFileNameRev1.getText()))
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
	
	
	// generate track with changed duration and shifted coordinates
	private boolean generateTrack(GPSTrack track, Date fromDate, Date toDate, 
									double deltaCoord, int maxNewPointsPerSection, String outFileName)
	{
		// change track duration
		track.changeDuration(fromDate, toDate);
		// shift points coordinates
		track.shiftCoordinates(deltaCoord);
		// extend track with new intremediate points
		track.extendTrackRandomly(maxNewPointsPerSection);
		// write track to .gpx-file
		return track.writeGpxFile(outFileName);
	}
	
	
	// check if fromDate < toDate 
	// and date of revision 1 < date of revision 2
	private boolean checkDatesValid()
	{
		Date fromDateRev1 = (Date)spinDateFromRev1.getValue();
		Date toDateRev1 = (Date)spinDateToRev1.getValue();
		Date fromDateRev2 = (Date)spinDateFromRev2.getValue();
		Date toDateRev2 = (Date)spinDateToRev2.getValue();
		// revision 1 dates
		if (fromDateRev1.getTime() >= toDateRev1.getTime())
		{
			
			JOptionPane.showMessageDialog(this, "Дата и время начала затирки должны быть меньше даты и времени окончания", 
											"Некорректные даты затирки", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		// revision 2 dates
		if (fromDateRev2.getTime() >= toDateRev2.getTime())
		{
			JOptionPane.showMessageDialog(this, "Дата и время начала учета должны быть меньше даты и времени окончания", 
											"Некорректные даты учета", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		// revision 1 date from < revision 2 date from
		if (fromDateRev1.getTime() >= fromDateRev2.getTime())
		{
			JOptionPane.showMessageDialog(this, "Дата и время начала затирки должны быть меньше даты и времени начала учета", 
											"Некорректные даты", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	
	// generate all tracks (revision 1 and revision 2)
	private void generateAllTracks()
	{
		// check all file paths
		if (!checkAllFilePaths())
			return;
		// check if dates are valid
		if (!checkDatesValid())
			return;
		
		String dateTimeFormat = "-dd.MM.yyyy-HH:mm:ss";
		// get data from controls
		String sourcFileName = txtSrcFileName.getText();
		String deviceName = (String)cbDeviceName.getSelectedItem();
		// revision 1 data
		Date fromDateRev1 = (Date)spinDateFromRev1.getValue();
		Date toDateRev1 = (Date)spinDateToRev1.getValue();
		Double deltaCoordRev1 = Double.parseDouble(txtMaxDeviationRev1.getText());
		String outFileNameRev1 = txtFileNameRev1.getText();
		String trackNameRev1 = txtTrackName.getText() + 
								GPSTrack.convertDateToString(fromDateRev1, dateTimeFormat);
		int maxMiddleCountRev1 = (Integer)spinPointsPerSectionRev1.getValue();
		// revision 1 data
		Date fromDateRev2 = (Date)spinDateFromRev2.getValue();
		Date toDateRev2 = (Date)spinDateToRev2.getValue();
		Double deltaCoordRev2 = Double.parseDouble(txtMaxDeviationRev2.getText());
		String outFileNameRev2 = txtFileNameRev2.getText();
		String trackNameRev2 = txtTrackName.getText() + 
								GPSTrack.convertDateToString(fromDateRev2, dateTimeFormat);
		int maxMiddleCountRev2 = (Integer)spinPointsPerSectionRev2.getValue();
		
		// load .gpx-file and create track
		GPSTrack trackRev1 = new GPSTrack(sourcFileName, deviceName, "");
		trackRev1.setTrackName(trackNameRev1);
		// clone track
		GPSTrack trackRev2 = trackRev1.clone();
		trackRev2.setTrackName(trackNameRev2);
		
		// make revision 1 track
		if (!generateTrack(trackRev1, fromDateRev1, toDateRev1, deltaCoordRev1, maxMiddleCountRev1, outFileNameRev1))
		{
			JOptionPane.showMessageDialog(this, "Не удалось сохранить трек затирки", 
											"Ошибка записи", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// make revision 2 track		
		if (!generateTrack(trackRev2, fromDateRev2, toDateRev2, deltaCoordRev2, maxMiddleCountRev2, outFileNameRev2))
		{
			JOptionPane.showMessageDialog(this, "Не удалось сохранить трек учета", 
											"Ошибка записи", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// show success dialog
		JOptionPane.showMessageDialog(this, "Измененные треки успешно сохранены", 
										"Информация", JOptionPane.INFORMATION_MESSAGE);
	}
}
