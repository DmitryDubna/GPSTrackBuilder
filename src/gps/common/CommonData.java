package gps.common;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

public class CommonData
{
	public static final String MSG_SRC_FILE_NOT_EXISTS =
			"Неверное имя исходного файла: \n     файл <%s> не существует\n";
	public static final String MSG_OUT_PATH_NOT_CORRECT = 
			"Неверный путь к выходному файлу %s: \n     указана "
			+ "несуществующая папка или некорректное имя файла\n";
	public static final String REV1_DEVIATION = "0.0005";
	public static final String REV2_DEVIATION = "0.00005";
	
	public static final String DATE_TIME_FORMAT = "dd MMMM yyyy   HH:mm:ss";
	
	public static final String[] getDeviceNames()
	{ 
		return new String[] {
			"Astro 220",
			"Astro 320",
			"Astro 430",
			"Gpsmap 60",
			"Gpsmap 60csx",
			"Gpsmap 62s",
			"Gpsmap 64",
			"Gpsmap 64st",
			"Gpsmap 78",
			"Oregon 450",
			"Oregon 550",
			"Oregon 600",
			"Oregon 650",
			"Oregon 700"
		};
	};	
	
	
	// get current directory
	public static String getCurrentDir()
	{
		return System.getProperty("user.dir") + File.separator;
	}
	
	
	// check source file existing
	public static boolean checkSourceFileExists(String fileName)
	{
		File file = new File(fileName);
		return (file.isFile());
	}
	
	
	// check if file directory exists and file name end with .gpx
	public static boolean checkOutPathCorrect(String fileName)
	{
		File file = new File(fileName);
		File dir = file.getParentFile();
		return ((dir != null) && dir.isDirectory() && fileName.endsWith(".gpx"));
	}
	
	
	public static long getMinutesBetween(Date fromDate, Date toDate)
	{
		return TimeUnit.MILLISECONDS.toMinutes(toDate.getTime() - fromDate.getTime());
	}
	
	
	// localize file chooser to russian
	public static void localizeFileChooserToRussian()
	{
		UIManager.put("FileChooser.fileNameLabelText", "Имя файла:");
		UIManager.put("FileChooser.lookInLabelText", "Смотреть в");
		UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов:");
		UIManager.put("FileChooser.upFolderToolTipText", "На один уровень вверх");
		UIManager.put("FileChooser.homeFolderToolTipText", "Домой");
		UIManager.put("FileChooser.newFolderToolTipText", "Создать новую папку");
		UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
		UIManager.put("FileChooser.detailsViewButtonToolTipText", "Подробно");
		UIManager.put("FileChooser.fileNameHeaderText", "Имя");
		UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
		UIManager.put("FileChooser.fileTypeHeaderText", "Тип");
		UIManager.put("FileChooser.fileDateHeaderText", "Изменен");
		UIManager.put("FileChooser.fileAttrHeaderText", "Атрибуты");
		UIManager.put("FileChooser.fileSizeKiloBytes", "{0} Кб");
		UIManager.put("FileChooser.fileSizeMegaBytes", "{0} Мб");
		UIManager.put("FileChooser.fileSizeGigaBytes", "{0} Гб");
		UIManager.put("FileChooser.viewMenuLabelText", "Настроить вид");
		UIManager.put("FileChooser.listViewActionLabelText", "Список");
		UIManager.put("FileChooser.detailsViewActionLabelText", "Подробно");
		UIManager.put("FileChooser.refreshActionLabelText", "Обновить");
		UIManager.put("FileChooser.newFolderActionLabelText", "Новая папка");
		UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
		UIManager.put("FileChooser.saveButtonText", "Сохранить");
		UIManager.put("FileChooser.openButtonText", "Открыть");
		UIManager.put("FileChooser.openButtonText", "Открыть");
		UIManager.put("FileChooser.cancelButtonText", "Отмена");
		UIManager.put("FileChooser.updateButtonText", "Обновить");
		UIManager.put("FileChooser.helpButtonText", "Помощь");
		UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
		UIManager.put("FileChooser.openButtonToolTipText", "Открыть");
		UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");
		UIManager.put("FileChooser.updateButtonToolTipText", "Обновить");
		UIManager.put("FileChooser.helpButtonToolTipText", "Помощь");
		UIManager.put("FileChooser.openDialogTitleText", "Выбрать исходный файл");
		UIManager.put("FileChooser.saveDialogTitleText", "Выбрать выходной файл");
	}
	
	
	// select look and feel
	public static void selectLookAndFeel()
	{
		String lafName = UIManager.getSystemLookAndFeelClassName();
		LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		for(LookAndFeelInfo l : lafs)
			if(l.getClassName().contains("Nimbus"))
			{
				lafName = l.getClassName();
				break;
			}
		try
		{
			UIManager.setLookAndFeel(lafName);
		}
		catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
	}
}
