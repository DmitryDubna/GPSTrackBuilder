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


public class GPSTrackBuilderFrame extends JFrame
{
	private static final long serialVersionUID = 2698799947780583769L;
	
	
	
	private JComboBox<String> cbDeviceName;
	private JTextField txtSrcFileName;
	private JButton btnChooseSrcFile;
	private JTextField txtTrackName;
	private JSpinner spinHeight;
	private JTextField txtHeightDeviation;
	private JSpinner spinPointsPerSection;
	
	private JTextField txtRev1FileName;
	private JButton btnChooseRev1File;
	private JSpinner spinRev1DateFrom;
	private JSpinner spinRev1DateTo;
	private JTextField txtMaxRev1Deviation;
	
	private JTextField txtRev2FileName;
	private JButton btnChooseRev2File;
	private JSpinner spinRev2DateFrom;
	private JSpinner spinRev2DateTo;
	private JTextField txtMaxRev2Deviation;
	
	private JButton btnGenerateAll;
	
	
	// main
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() 
			{
				@Override
				public void run() 
				{
					GPSTrackBuilderFrame frame = new GPSTrackBuilderFrame();
					frame.setVisible(true);
				}
			});
	}
	
	
	// constructor
	public GPSTrackBuilderFrame() 
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700, 700);
		setTitle("Преобразователь GPS-трека");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		// set "nimbus" look and feel
		CommonData.selectLookAndFeel();
		// init controls
		initControls();
		// localize file chooser component to russian
		CommonData.localizeFileChooserToRussian();
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
		txtSrcFileName = new JTextField(CommonData.getCurrentDir() + "gpx" + File.separator + "86.1.gpx");
		btnChooseSrcFile = new JButton();
		txtTrackName = new JTextField("Трек");
		spinHeight = new JSpinner(new SpinnerNumberModel(250.0, -500.0, 500.0, 0.1));
		txtHeightDeviation = new JTextField("1.0");
		spinPointsPerSection = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
		// add controls
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weightx = 0.1;
		pnlSourceData.add(new JLabel("Модель навигатора"), gbc);
		pnlSourceData.add(new JLabel("Имя исходного файла (.gpx)"), gbc);
		pnlSourceData.add(new JLabel("Наименование трека"), gbc);
		pnlSourceData.add(new JLabel("Высота над уровнем моря"), gbc);
		pnlSourceData.add(new JLabel("Отклонение высоты"), gbc);
		pnlSourceData.add(new JLabel("Число промежуточных точек"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 2;
		pnlSourceData.add(cbDeviceName, gbc);
		pnlSourceData.add(makeChooseFileBox(txtSrcFileName, btnChooseSrcFile), gbc);
		pnlSourceData.add(txtTrackName, gbc);
		pnlSourceData.add(spinHeight, gbc);
		pnlSourceData.add(txtHeightDeviation, gbc);
		pnlSourceData.add(spinPointsPerSection, gbc);
		return pnlSourceData;
	}
	
	
	// create revision 1 data panel
	private JPanel createRev1DataPanel()
	{
		// initialize calendar
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// panel with title
		JPanel pnlRev1Data = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		pnlRev1Data.setBorder(new TitledBorder("Данные затирки"));
		// create controls
		// revision 1 file name
		txtRev1FileName = new JTextField(CommonData.getCurrentDir() + "rev1.gpx");
		btnChooseRev1File = new JButton();
		// revision 1 date and time from
		spinRev1DateFrom = new JSpinner(new SpinnerDateModel());
		spinRev1DateFrom.setEditor(new JSpinner.DateEditor(spinRev1DateFrom, CommonData.DATE_TIME_FORMAT));
		spinRev1DateFrom.setValue(cal.getTime());
		// revision 1 date and time to		
		spinRev1DateTo = new JSpinner(new SpinnerDateModel());
		spinRev1DateTo.setEditor(new JSpinner.DateEditor(spinRev1DateTo, CommonData.DATE_TIME_FORMAT));
		cal.add(Calendar.HOUR, 3);
		spinRev1DateTo.setValue(cal.getTime());
		// max coordinate deviation
		txtMaxRev1Deviation = new JTextField(CommonData.REV1_DEVIATION);
		// add controls
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weightx = 0.1;
		pnlRev1Data.add(new JLabel("Имя выходного файла (.gpx)"), gbc);
		pnlRev1Data.add(new JLabel("Дата и время начала"), gbc);
		pnlRev1Data.add(new JLabel("Дата и время окончания"), gbc);
		pnlRev1Data.add(new JLabel("Отклонение координат"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 2;
		pnlRev1Data.add(makeChooseFileBox(txtRev1FileName, btnChooseRev1File), gbc);
		pnlRev1Data.add(spinRev1DateFrom, gbc);
		pnlRev1Data.add(spinRev1DateTo, gbc);
		pnlRev1Data.add(txtMaxRev1Deviation, gbc);
		return pnlRev1Data;
	}
	
	
	// create revision 1 data panel
	private JPanel createRev2DataPanel()
	{
		// initialize calendar
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// panel with title
		JPanel pnlRev2Data = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		pnlRev2Data.setBorder(new TitledBorder("Данные учета"));
		// create controls
		// revision 2 file name
		txtRev2FileName = new JTextField(CommonData.getCurrentDir() + "rev2.gpx");
		btnChooseRev2File = new JButton();
		// revision 2 date and time from
		spinRev2DateFrom = new JSpinner(new SpinnerDateModel());
		spinRev2DateFrom.setEditor(new JSpinner.DateEditor(spinRev2DateFrom, CommonData.DATE_TIME_FORMAT));
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.HOUR, 3);
		spinRev2DateFrom.setValue(cal.getTime());
		// revision 2 date and time to		
		spinRev2DateTo = new JSpinner(new SpinnerDateModel());
		spinRev2DateTo.setEditor(new JSpinner.DateEditor(spinRev2DateTo, CommonData.DATE_TIME_FORMAT));
		cal.add(Calendar.HOUR, 3);
		spinRev2DateTo.setValue(cal.getTime());
		// max coordinate deviation
		txtMaxRev2Deviation = new JTextField(CommonData.REV2_DEVIATION);
		// add controls
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weightx = 0.1;
		pnlRev2Data.add(new JLabel("Имя выходного файла (.gpx)"), gbc);
		pnlRev2Data.add(new JLabel("Дата и время начала"), gbc);
		pnlRev2Data.add(new JLabel("Дата и время окончания"), gbc);
		pnlRev2Data.add(new JLabel("Отклонение координат"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 2;
		pnlRev2Data.add(makeChooseFileBox(txtRev2FileName, btnChooseRev2File), gbc);
		pnlRev2Data.add(spinRev2DateFrom, gbc);
		pnlRev2Data.add(spinRev2DateTo, gbc);		
		pnlRev2Data.add(txtMaxRev2Deviation, gbc);
		return pnlRev2Data;
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
		btnGenerateAll = new JButton("Создать все треки");
		btnGenerateAll.addActionListener(e -> generateAll());
		Box boxBottom = Box.createHorizontalBox();
		boxBottom.add(Box.createHorizontalGlue());
		boxBottom.add(btnGenerateAll);
		boxBottom.add(Box.createRigidArea(new Dimension(15, 40)));
		// add all to frame
		add(pnlSourceData);
		add(pnlRev1Data);
		add(pnlRev2Data);
		add(boxBottom);
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

	
	// get Date from JSpinner
	private Date getDateFromControl(JSpinner spinner)
	{
		return (Date) spinner.getValue();
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
		if (!CommonData.checkOutPathCorrect(txtRev1FileName.getText()))
		{
			result = false;
			errMsg += String.format(CommonData.MSG_OUT_PATH_NOT_CORRECT, "затирки");
		}
		if (!CommonData.checkOutPathCorrect(txtRev2FileName.getText()))
		{
			result = false;
			errMsg += String.format(CommonData.MSG_OUT_PATH_NOT_CORRECT, "учета");
		}
		if (!result)
		{
			JOptionPane.showMessageDialog(this, errMsg, "Ошибка ввода данных", JOptionPane.ERROR_MESSAGE);
		}
		return result;
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
	
	// generate all tracks
	private void generateAll()
	{
		// check all file paths
		if (!checkAllFilePaths())
			return;
		//
		// load .gpx-file and create track
		GPSTrack track = new GPSTrack(txtSrcFileName.getText(), 
									  (String)cbDeviceName.getSelectedItem(),
									  txtTrackName.getText());
		//
		// create new track for revision 1
		GPSTrack trackRev1 = track.makeTrackWithDeviation(
								getDateFromControl(spinRev1DateFrom), 
								getDateFromControl(spinRev1DateTo),
								Double.parseDouble(txtMaxRev1Deviation.getText()),
								(Double)spinHeight.getValue(),
								Double.parseDouble(txtHeightDeviation.getText()));
		//
		// get number of intermediate points
		int middleCount = (Integer)spinPointsPerSection.getValue();
		// get extended track for revision 1
		GPSTrack trackRev1Ext = trackRev1.makeExtendedTrack(middleCount);
		//
		// create new track for revision 2
		GPSTrack trackRev2 = trackRev1.makeTrackWithDeviation(
								getDateFromControl(spinRev2DateFrom), 
								getDateFromControl(spinRev2DateTo), 
								Double.parseDouble(txtMaxRev2Deviation.getText()),
								(Double)spinHeight.getValue(),
								Double.parseDouble(txtHeightDeviation.getText()));
		// get extended track for revision 2
		GPSTrack trackRev2Ext = trackRev2.makeExtendedTrack(middleCount);
		// interpolate heights
		GPSTrack.interpolateHeights(trackRev1Ext.getPoints(), trackRev2Ext.getPoints());
		
		// write track for revision 1 to .gpx-file
		if (!trackRev1Ext.writeGpxFile(txtRev1FileName.getText()))
		{
			JOptionPane.showMessageDialog(this, "Не удалось сохранить трек затирки", "Ошибка записи", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// write track for revision 2 to .gpx-file
		if (!trackRev2Ext.writeGpxFile(txtRev2FileName.getText()))
		{
			JOptionPane.showMessageDialog(this, "Не удалось сохранить трек учета", "Ошибка записи", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// show success dialog
		JOptionPane.showMessageDialog(this, "Треки затирки и учета успешно сохранены", "Информация", JOptionPane.INFORMATION_MESSAGE);
	}
}
