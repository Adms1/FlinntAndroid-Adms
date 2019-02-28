package com.edu.flinnt.models.store;

import com.edu.flinnt.customviews.store.expandableRecylerview.ExpandableGroup;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FilterDataModel extends ExpandableGroup {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public FilterDataModel(String title, List items) {
        super(title, items);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Author {

        @SerializedName("author_id")
        @Expose
        private Integer authorId;
        @SerializedName("author_name")
        @Expose
        private String authorName;

        private boolean isChecked;

        public Integer getAuthorId() {
            return authorId;
        }

        public void setAuthorId(Integer authorId) {
            this.authorId = authorId;
        }

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }


    public static  class Board {

        @SerializedName("board_id")
        @Expose
        private Integer boardId;
        @SerializedName("board_name")
        @Expose
        private String boardName;

        private boolean isChecked;


        public Integer getBoardId() {
            return boardId;
        }

        public void setBoardId(Integer boardId) {
            this.boardId = boardId;
        }

        public String getBoardName() {
            return boardName;
        }

        public void setBoardName(String boardName) {
            this.boardName = boardName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public class Category {

        @SerializedName("category_tree_id")
        @Expose
        private Integer categoryTreeId;
        @SerializedName("child_category_id")
        @Expose
        private Integer childCategoryId;
        @SerializedName("parent_category_id")
        @Expose
        private Integer parentCategoryId;
        @SerializedName("category_name")
        @Expose
        private String categoryName;
        private boolean isChecked;


        public Integer getCategoryTreeId() {
            return categoryTreeId;
        }

        public void setCategoryTreeId(Integer categoryTreeId) {
            this.categoryTreeId = categoryTreeId;
        }

        public Integer getChildCategoryId() {
            return childCategoryId;
        }

        public void setChildCategoryId(Integer childCategoryId) {
            this.childCategoryId = childCategoryId;
        }

        public Integer getParentCategoryId() {
            return parentCategoryId;
        }

        public void setParentCategoryId(Integer parentCategoryId) {
            this.parentCategoryId = parentCategoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }


    public class Data extends ExpandableGroup{

        @SerializedName("authors")
        @Expose
        private List<Author> authors = null;
        @SerializedName("boards")
        @Expose
        private List<Board> boards = null;
        @SerializedName("languages")
        @Expose
        private List<Language> languages = null;
        @SerializedName("publishers")
        @Expose
        private List<Publisher> publishers = null;
        @SerializedName("format")
        @Expose
        private Format format;
        @SerializedName("categories")
        @Expose
        private List<Category> categories = null;

        public Data(String title, List items) {
            super(title, items);
        }

        public List<Author> getAuthors() {
            return authors;
        }

        public void setAuthors(List<Author> authors) {
            this.authors = authors;
        }

        public List<Board> getBoards() {
            return boards;
        }

        public void setBoards(List<Board> boards) {
            this.boards = boards;
        }

        public List<Language> getLanguages() {
            return languages;
        }

        public void setLanguages(List<Language> languages) {
            this.languages = languages;
        }

        public List<Publisher> getPublishers() {
            return publishers;
        }

        public void setPublishers(List<Publisher> publishers) {
            this.publishers = publishers;
        }

        public Format getFormat() {
            return format;
        }

        public void setFormat(Format format) {
            this.format = format;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public void setCategories(List<Category> categories) {
            this.categories = categories;
        }

    }


    public static class Format extends ExpandableGroup {

        @SerializedName("paperback")
        @Expose
        private String paperback;
        @SerializedName("kindal")
        @Expose
        private String kindal;
        @SerializedName("hardcover")
        @Expose
        private String hardcover;
        @SerializedName("boardbook")
        @Expose
        private String boardbook;

        public Format(String title, List items) {
            super(title, items);
        }

        public String getPaperback() {
            return paperback;
        }

        public void setPaperback(String paperback) {
            this.paperback = paperback;
        }

        public String getKindal() {
            return kindal;
        }

        public void setKindal(String kindal) {
            this.kindal = kindal;
        }

        public String getHardcover() {
            return hardcover;
        }

        public void setHardcover(String hardcover) {
            this.hardcover = hardcover;
        }

        public String getBoardbook() {
            return boardbook;
        }

        public void setBoardbook(String boardbook) {
            this.boardbook = boardbook;
        }

    }


    public static class BookFormatDataModel {
        private String key;
        private String value;
        private boolean isChecked;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public class Language extends ExpandableGroup {

        @SerializedName("language_id")
        @Expose
        private Integer languageId;
        @SerializedName("language_name")
        @Expose
        private String languageName;

        private boolean isChecked;

        public Language(String title, List items) {
            super(title, items);
        }

        public Integer getLanguageId() {
            return languageId;
        }

        public void setLanguageId(Integer languageId) {
            this.languageId = languageId;
        }

        public String getLanguageName() {
            return languageName;
        }

        public void setLanguageName(String languageName) {
            this.languageName = languageName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public class Publisher {

        @SerializedName("publisher_id")
        @Expose
        private Integer publisherId;
        @SerializedName("publisher_name")
        @Expose
        private String publisherName;

        private boolean isChecked;

        public Integer getPublisherId() {
            return publisherId;
        }

        public void setPublisherId(Integer publisherId) {
            this.publisherId = publisherId;
        }

        public String getPublisherName() {
            return publisherName;
        }

        public void setPublisherName(String publisherName) {
            this.publisherName = publisherName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
