package com.romy.platform.main.common.mapper;


import com.romy.platform.main.common.dvo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface FolderMapper {

    List<FolderChildDvo> selectFolderChilds(@Param("folderCd") String folderCd, @Param("tableNm") String tableNm);

    List<FolderChildDvo> selectFolderChildsForCut(@Param("folderCd") String folderCd, @Param("tableNm") String tableNm);

    int insertFolderDownloadHistory(FolderHistoryDvo dvo);

    int insertFolderFileUpDownHistory(FolderUpDownHistDvo dvo);

    String selectFolderUpDownCode(@Param("upRefCd") String upRefCd, @Param("udType") String udType
            , @Param("folderGrp") String folderGrp);

    int updateFolderUpDownEndType(@Param("fafUdCd") String fafUdCd, @Param("endType") String endType);

    List<FolderTreeDvo> selectFolderAllTrees(@Param("folderGrp") String folderGrp, @Param("sectorCd") String sectorCd);
    List<FolderTreeDvo> selectFolderAllTreesForUpdate(@Param("folderGrp") String folderGrp, @Param("sectorCd") String sectorCd);

    int mergeFolderFileUpDownHistory(FolderUpDownHistDvo dvo);

    FolderDvo selectFolderInfo(@Param("folderCd") String folderCd);
    FileAttFolderDvo selectProjectFolderInfo(@Param("folderCd") String folderCd);

    int selectOrdNumMaxByParentCd(@Param("parentCd") String parentCd);

    void insertFolder(FolderDvo dvo);

    int insertFileAttFolder(FileAttFolderDvo dvo);

    void updateFolderName(@Param("folderCd") String folderCd, @Param("folderNm") String folderNm);

    String selectNoPermissionFolder(@Param("folderCd") String folderCd);
    List<String> selectNoPermissionFolders(@Param("folderCd") String folderCd, @Param("folderGrp") String folderGrp);
    List<String> selectNoPermissionProjectFolders(@Param("folderCd") String folderCd);

    String selectFolderPath(@Param("folderCd") String folderCd);
    String selectFolderRootPath(@Param("folderCd") String folderCd);
    void deleteFolder(@Param("folderCd") String folderCd);
    void deleteProjectFolder(@Param("folderCd") String folderCd);

    int updateParentFolderCd(@Param("dvo") FolderDvo dvo, @Param("list") List<String> folderCds);

    void updateFolder(FolderDvo dvo);
    void updateFolderOnlyField(FolderDvo dvo);
    void updateProjectFolderOnlyField(FileAttFolderDvo dvo);

    long selectFolderSize(@Param("folderCd") String folderCd, @Param("sectorCd") String sectorCd, @Param("finalYn") String finalYn);

}
