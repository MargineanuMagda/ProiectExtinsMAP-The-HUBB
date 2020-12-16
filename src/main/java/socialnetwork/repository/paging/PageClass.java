package socialnetwork.repository.paging;

import java.util.stream.Stream;

public class PageClass<T>  implements Page<T>{
    private Pageable pageable;
    private Stream<T> content;

    public PageClass(Pageable pageable, Stream<T> content) {
        this.pageable = pageable;
        this.content = content;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    @Override
    public Pageable nextPageable() {
        return new PageableClass(this.pageable.getPageNumber()+1,this.pageable.getPageSize());
    }

    @Override
    public Stream<T> getContent() {
        return content;
    }
}
