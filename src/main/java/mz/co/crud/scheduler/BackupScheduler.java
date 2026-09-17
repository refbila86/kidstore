package mz.co.crud.scheduler;

import mz.co.crud.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupScheduler {

    @Autowired private BackupService backupService;
    @Value("${backup.auto.enabled:true}") private boolean autoEnabled;

    @Scheduled(cron = "${backup.auto.cron:0 0 22 * * *}", zone = "${backup.auto.timezone:Africa/Maputo}")
    public void backupDiario22h() {
        if (!autoEnabled) return;
        try {
            System.out.println("[BACKUP AUTO 22h] Iniciando...");
            backupService.gerarBackupAutomatico();
        } catch (Exception e) {
            System.err.println("[BACKUP] Erro: " + e.getMessage());
        }
    }
}