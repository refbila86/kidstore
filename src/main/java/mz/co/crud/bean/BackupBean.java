package mz.co.crud.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import mz.co.crud.service.BackupService;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ViewScoped
public class BackupBean implements Serializable
{

	@Autowired
	private BackupService backupService;
	private List<BackupInfo> backups = new ArrayList<>();

	@PostConstruct
	public void init()
	{
		System.out.println("[BackupBean] INIT - Dir: " + getBackupDir());
		listarBackups();
	}

	public void gerarBackup()
	{
		try
		{
			String file = backupService.gerarBackupAutomatico();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Gerado: " + file));
			listarBackups();
		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro mysqldump", e.getMessage()));
		}
	}

	public void listarBackups()
	{
		try
		{
			Path dir = Paths.get(getBackupDir());
			Files.createDirectories(dir);
			if (!Files.exists(dir))
			{
				System.out.println("[BackupBean] Pasta não existe: " + dir);
				backups = new ArrayList<>();
				return;
			}
			backups = Files.list(dir).filter(p -> p.getFileName().toString().endsWith(".sql"))
					.sorted(Comparator.comparingLong((Path p) -> p.toFile().lastModified()).reversed()).map(p ->
					{
						try
						{
							return new BackupInfo(p.getFileName().toString(), p.toString(), new Date(p.toFile().lastModified()), formatSize(Files.size(p)));
						} catch (Exception ex)
						{
							return null;
						}
					}).filter(Objects::nonNull).collect(Collectors.toList());
			System.out.println("[BackupBean] Encontrados " + backups.size() + " backups em " + dir);
		} catch (Exception e)
		{
			e.printStackTrace();
			backups = new ArrayList<>();
		}
	}

	public StreamedContent downloadFile(BackupInfo b)
	{
		return backupService.downloadFile(b);
	}

	public void deletar(BackupInfo b)
	{
		try
		{
			Files.deleteIfExists(Paths.get(b.getCaminhoCompleto()));
			listarBackups();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Removido"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String formatSize(long bytes)
	{
		if (bytes < 1024)
			return bytes + " B";
		if (bytes < 1024 * 1024)
			return String.format("%.1f KB", bytes / 1024.0);
		return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
	}

	public List<BackupInfo> getBackups()
	{
		return backups;
	}

	public String getBackupDir()
	{
		return backupService.getBackupDir();
	}

	public static class BackupInfo implements Serializable
	{
		private String nome, caminhoCompleto, tamanho;
		private Date data;

		public BackupInfo(String n, String c, Date d, String t)
		{
			nome = n;
			caminhoCompleto = c;
			data = d;
			tamanho = t;
		}

		public String getNome()
		{
			return nome;
		}

		public String getCaminhoCompleto()
		{
			return caminhoCompleto;
		}

		public Date getData()
		{
			return data;
		}

		public String getTamanho()
		{
			return tamanho;
		}
	}
}