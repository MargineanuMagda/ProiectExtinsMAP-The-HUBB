package socialnetwork.repository.paging;

public class PageableClass implements Pageable {
    private int pageNumber;
    private int pageSize;

    public PageableClass(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    @Override
    public int getPageNumber() {
        return this.pageNumber;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }
}
