package mz.co.crud.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mz.co.crud.bean.BackupBean;

@Service
public class BackupService
{

	@Value("${spring.datasource.url}")
	private String dbUrl;
	@Value("${spring.datasource.username}")
	private String dbUser;
	@Value("${spring.datasource.password}")
	private String dbPassword;

	private String backupDir = System.getProperty("user.home") + "/backups/sale_management";

	public String gerarBackupAutomatico() throws Exception
	{
		Files.createDirectories(Paths.get(backupDir));

		Files.createDirectories(Paths.get(backupDir));
		String dbName = extrairNomeBanco();  

		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String fileName = dbName + "_" + timestamp + "_AUTO.sql";
		Path backupFile = Paths.get(backupDir, fileName);

		String mysqldump = findMysqldump();
		System.out.println("Usando mysqldump: " + mysqldump);

		ProcessBuilder pb = new ProcessBuilder(mysqldump, "-u", dbUser, "--single-transaction", "--routines", "--triggers", dbName);
 		pb.environment().put("MYSQL_PWD", dbPassword);
		pb.redirectErrorStream(false);

		Process process = pb.start();

		StringBuilder errorLog = new StringBuilder();
		Thread errorThread = new Thread(() ->
		{
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream())))
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					errorLog.append(line).append("\n");
					System.err.println("[mysqldump ERR] " + line);
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		});
		errorThread.start();

		try (InputStream in = process.getInputStream(); OutputStream out = Files.newOutputStream(backupFile))
		{
			in.transferTo(out);
		}

		boolean finished = process.waitFor(60, TimeUnit.SECONDS);
		errorThread.join(2000);

		if (!finished || process.exitValue() != 0)
		{
			String error = errorLog.toString();
			Files.deleteIfExists(backupFile);
			throw new RuntimeException("mysqldump falhou: " + error + " | Código: " + process.exitValue());
		}

		System.out.println("[BACKUP] Criado: " + backupFile + " (" + Files.size(backupFile) + " bytes)");
		return fileName;
	}

	private String extrairNomeBanco()
	{
		// 1. Tira os parâmetros depois do?
		String semParams = dbUrl.split("\\?")[0];
		// 2. Pega depois do último /
		// jdbc:mysql://localhost:3306/kidstore -> kidstore
		String nome = semParams.substring(semParams.lastIndexOf("/") + 1);
		System.out.println("[BACKUP] URL: " + dbUrl + " -> Banco extraído: " + nome);
		return nome;
	}

	public StreamedContent downloadFile(BackupBean.BackupInfo b)
	{
		Path path = Paths.get(b.getCaminhoCompleto());
		return DefaultStreamedContent.builder().name(b.getNome()).contentType("application/octet-stream").stream(() ->
		{
			try
			{
				return Files.newInputStream(path);
			} catch (IOException e)
			{
				return null;
			}
		}).build();
	}

	private String findMysqldump()
	{
		// Tenta no PATH
		try
		{
			Process p = new ProcessBuilder(System.getProperty("os.name").toLowerCase().contains("win") ? "where" : "which", "mysqldump").start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String path = r.readLine();
			if (path != null && !path.isEmpty())
				return path.trim();
		} catch (Exception e)
		{
		}

		// Caminhos comuns Windows
		String[] winPaths =
		{ "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe", "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysqldump.exe",
				"C:\\xampp\\mysql\\bin\\mysqldump.exe", "C:\\laragon\\bin\\mysql\\mysql-8.0\\bin\\mysqldump.exe" };
		for (String p : winPaths)
		{
			if (Files.exists(Paths.get(p)))
				return p;
		}
		return "mysqldump"; // fallback
	}

	public String getBackupDir()
	{
		return backupDir;
	}
}