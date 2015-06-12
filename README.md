# JAVA-TAGS-ANGULAR
Custom tags for creating AngularJS views from Java POJOs

For example, for this DTO class:

```
package com.emergya.example;



public final class UserFormDto {

    long id;
    String nameVar;

    String name;

    String inviteEmail;

    String loginEmail;

    boolean admin;

    String registerToken;

    List<String> roles;

    String phone;

    public UserFormDto() {

    }

    public UserFormDto(UserEntity user) {
        this.setId(user.getId());
        this.setName(user.getName());
        this.setInviteEmail(user.getInviteEmail());
        this.setPhone(user.getPhone());

        this.setAdmin(user.isAdmin());
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    @NotEmpty
    @FormField(label = "Full Name", order = 10)
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the admin
     */
    @FormField(label = "Is Admin", type = FormWidgetType.CHECKBOX, order = 40)
    public boolean isAdmin() {
        return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * @return the inviteEmail
     */
    @Email
    @FormField(label = "Invite Email", order = 20)
    public String getInviteEmail() {
        return inviteEmail;
    }

    /**
     * @param inviteEmail the inviteEmail to set
     */
    public void setInviteEmail(String inviteEmail) {
        this.inviteEmail = inviteEmail;
    }

    /**
     * @return the phone
     */
    @FormField(label = "User phone", order = 30)
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

}


```

You can use the following tag:

```
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="ja" uri="/META-INF/tags/java-tags-angular" %>

<!-- Other JSP page content.. -->

<ja:entityForm className="com.emergya.example.UserFormDto" model="rowValues"></ja:entityForm>

<!-- Further JSP page content -->

```

And you'll get the following form:

![Example form](http://emergya.github.io/java-tags-angular/form.png)

TODO:

* More input types supported
* Refactor of the @FieldType annotations
* Other views: read-only view, some code already existing but needs review; and grid/table view, etc.
