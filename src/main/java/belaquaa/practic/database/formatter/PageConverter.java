package belaquaa.practic.database.formatter;

import belaquaa.practic.database.dto.PageDto;
import org.springframework.data.domain.Page;

public class PageConverter {

    public static <T> PageDto<T> toPageDto(Page<T> page) {
        PageDto<T> dto = new PageDto<>();
        dto.setContent(page.getContent());
        dto.setPageNumber(page.getNumber());
        dto.setPageSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        dto.setLast(page.isLast());
        dto.setFirst(page.isFirst());
        dto.setSort(page.getSort().toString());
        return dto;
    }
}