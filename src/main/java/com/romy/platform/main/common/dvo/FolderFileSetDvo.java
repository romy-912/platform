package com.romy.platform.main.common.dvo;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
public class FolderFileSetDvo {
    
    // 폴더 리스트
    private Set<FolderInfo> folders;
    // 파일리스트
    private List<FileInfo> files;
    // 물리 파일 리스트
    private List<Path> phyFiles;


    @Getter
    @Setter
    public static class FolderInfo {
        private String folderName;
        private int level;
        private String folderCd;
        private String parentCd;
        private String folderPath;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FolderInfo that)) return false;
            return level == that.level &&
                    Objects.equals(folderName, that.folderName) &&
                    Objects.equals(parentCd, that.parentCd) &&
                    Objects.equals(folderPath, that.folderPath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(folderName, level, parentCd, folderPath);
        }
    }

    @Getter
    @Setter
    public static class FileInfo {
        private String fileAttCd;
        private String fileName;
        private String fileExt;
        private String filePhyPath;
        private long fileSize;
        private String parentFolderCd;
        private String folderName;
    }

    
}
