package ltd.finelink.tool.disk.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import lombok.Data;

 
@Data
public class IPage<T> implements Serializable {

    private static final long serialVersionUID = 6813348202284922946L;

    private boolean hasNextPage;
    private List<T> list;
    private Integer nextPage;

    // count and  total 是相同的
    private Long count;
    private Long total;

    // 每页条数
    private Integer size;

    // 最后一个ID
    private String lastId;

    public IPage() {
    }

    public static <T> IPage<T> empty() {
        return of(Collections.emptyList(), false, 1, 0L, 0);
    }

    protected IPage(boolean hasNextPage, List<T> list, Integer nextPage, Long count, Integer size, String lastId) {
        this.hasNextPage = hasNextPage;
        this.list = list;
        this.nextPage = nextPage;
        this.count = count;
        this.size = size;
        this.total = count;
        this.lastId = lastId;
    }

     
    public static <T> IPage<T> of(List<T> list, boolean hastNextPage, Integer nextPage) {
        return of(list, hastNextPage, nextPage, null, null);
    }

    public static <T> IPage<T> of(List<T> list, boolean hastNextPage, Integer nextPage, String lastId) {
        return new IPage<T>(hastNextPage, list, nextPage, null, null, lastId);
    }

    public static <T> IPage<T> of(List<T> list, boolean hastNextPage, Integer nextPage, Long count, Integer size) {
        return new IPage<T>(hastNextPage, list, nextPage, count, size, null);
    }

    public <U> IPage<U> map(Function<List<T>, List<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (list == null) {
            return IPage.of(null, hasNextPage, nextPage, Optional.ofNullable(count).orElse(total), size);
        } else {
            return IPage.of(mapper.apply(list), hasNextPage, nextPage, Optional.ofNullable(count).orElse(total), size);
        }
    }
}
