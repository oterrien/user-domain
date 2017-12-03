package com.ote.user;

import com.ote.JsonUtils;
import com.ote.user.api.IUserRightsService;
import com.ote.user.api.PerimeterPath;
import com.ote.user.api.exception.ApplicationNotFoundException;
import com.ote.user.api.exception.RoleNotFoundException;
import com.ote.user.api.exception.UserNotFoundException;
import com.ote.user.api.model.*;
import com.ote.user.business.UserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UserDomainTest {

    @Test
    public void testPerimeterPath() {

        PerimeterPath path = PerimeterPath.builder();

        SoftAssertions assertions = new SoftAssertions();

        path = path.startsWith("Deal").build();
        assertions.assertThat(path.toString()).isEqualTo("Deal");

        path = path.startsWith("Deal").then("GLE").build();
        assertions.assertThat(path.toString()).isEqualTo("Deal/GLE");

        path = path.startsWith("Deal").then("GLE").then("Dash").build();
        assertions.assertThat(path.toString()).isEqualTo("Deal/GLE/Dash");

        path = path.startsWith("Deal").then("GLE", "Dash", "LGE").build();
        assertions.assertThat(path.toString()).isEqualTo("Deal/GLE/Dash/LGE");

        assertions.assertAll();
    }

    @Test
    public void testUserRightStructure() throws IOException {

        UserRights userRights = new UserRights(new User("rene.barjavel"), new Application("SLA"));
        Perimeter dealPerimeter = new Perimeter("Deal");
        Perimeter glePerimeter = new Perimeter("GLE");
        glePerimeter.getPrivileges().add(new Privilege("ReadWrite"));
        dealPerimeter.getChildren().add(glePerimeter);
        dealPerimeter.getPrivileges().add(new Privilege("ReadOnly"));
        userRights.getPerimeters().add(dealPerimeter);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(userRights).isNotNull();
        assertions.assertThat(userRights.getUser().getLogin()).isEqualTo("rene.barjavel");
        assertions.assertThat(userRights.getApplication().getCode()).isEqualTo("SLA");
        assertions.assertThat(userRights.getPerimeters()).hasSize(1);
        assertions.assertThat(userRights.getPerimeters().get(0).getCode()).isEqualTo("Deal");
        assertions.assertThat(userRights.getPerimeters().get(0).getPrivileges()).hasSize(1);
        assertions.assertThat(userRights.getPerimeters().get(0).getPrivileges().get(0).getCode()).isEqualTo("ReadOnly");
        assertions.assertThat(userRights.getPerimeters().get(0).getChildren()).hasSize(1);
        assertions.assertThat(userRights.getPerimeters().get(0).getChildren().get(0).getCode()).isEqualTo("GLE");
        assertions.assertThat(userRights.getPerimeters().get(0).getChildren().get(0).getPrivileges()).hasSize(1);
        assertions.assertThat(userRights.getPerimeters().get(0).getChildren().get(0).getPrivileges().get(0).getCode()).isEqualTo("ReadWrite");
        assertions.assertAll();

        log.debug(JsonUtils.serialize(userRights));
    }

    @Test
    public void testUserNotFoundException() {

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        User user = new User("rene.barjavel");
        Application application = new Application("SLA");
        UserRights userRights = new UserRights(user, application);
        userRights.getPerimeters().add(new Perimeter("Deal"));
        userRightsRepositoryMock.getUserRightsList().add(userRights);

        Assertions.assertThatThrownBy(() -> userRightsService.getPrivileges(new User("NONE"), new Application("SLA"), PerimeterPath.builder().startsWith("Deal").build())).
                isInstanceOf(UserNotFoundException.class);

    }

    @Test
    public void testApplicationNotFoundException() {

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        User user = new User("rene.barjavel");
        Application application = new Application("SLA");
        UserRights userRights = new UserRights(user, application);
        userRights.getPerimeters().add(new Perimeter("Deal"));
        userRightsRepositoryMock.getUserRightsList().add(userRights);

        Assertions.assertThatThrownBy(() -> userRightsService.getPrivileges(new User("rene.barjavel"), new Application("NONE"), PerimeterPath.builder().startsWith("Deal").build())).
                isInstanceOf(ApplicationNotFoundException.class);
    }

    @Test
    public void testApplicationNotFoundExceptionForUser() {

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        User user = new User("rene.barjavel");
        Application application = new Application("GLE");
        UserRights userRights = new UserRights(user, application);
        userRights.getPerimeters().add(new Perimeter("Deal"));
        userRightsRepositoryMock.getUserRightsList().add(userRights);

        user = new User("bernard.werber");
        application = new Application("SLA");
        userRights = new UserRights(user, application);
        userRights.getPerimeters().add(new Perimeter("Deal"));
        userRightsRepositoryMock.getUserRightsList().add(userRights);


        Assertions.assertThatThrownBy(() -> userRightsService.getPrivileges(new User("rene.barjavel"), new Application("SLA"), PerimeterPath.builder().startsWith("Deal").build())).
                isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    public void testPerimeterPathIsNotDefinedForAnApplication() {

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        UserRights userRights = new UserRights(new User("rene.barjavel"), new Application("SLA"));
        Perimeter dealPerimeter = new Perimeter("Deal");
        Perimeter glePerimeter = new Perimeter("GLE");
        glePerimeter.getPrivileges().add(new Privilege("ReadWrite"));
        dealPerimeter.getChildren().add(glePerimeter);
        dealPerimeter.getPrivileges().add(new Privilege("ReadOnly"));
        userRights.getPerimeters().add(dealPerimeter);

        userRightsRepositoryMock.getUserRightsList().add(userRights);

        Assertions.assertThatThrownBy(() -> userRightsService.getPrivileges(new User("rene.barjavel"), new Application("SLA"), PerimeterPath.builder().startsWith("NONE").then("NONE").build())).
                isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    public void testRoleIsNotDefinedForAUserAndAnApplication() {

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        UserRights userRights = new UserRights(new User("rene.barjavel"), new Application("SLA"));
        userRightsRepositoryMock.getUserRightsList().add(userRights);

        Assertions.assertThatThrownBy(() -> userRightsService.getPrivileges(new User("rene.barjavel"), new Application("SLA"), PerimeterPath.builder().startsWith("NONE").then("NONE").build())).
                isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    public void testPerimeterPathIsDefinedForAnApplication() throws Exception {

        UserRights userRights = new UserRights(new User("rene.barjavel"), new Application("SLA"));
        Perimeter dealPerimeter = new Perimeter("Deal");
        Perimeter glePerimeter = new Perimeter("GLE");
        glePerimeter.getPrivileges().add(new Privilege("ReadWrite"));
        dealPerimeter.getChildren().add(glePerimeter);
        dealPerimeter.getPrivileges().add(new Privilege("ReadOnly"));
        userRights.getPerimeters().add(dealPerimeter);

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        userRightsRepositoryMock.getUserRightsList().add(userRights);

        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        SoftAssertions assertions = new SoftAssertions();

        // Check with path Deal
        List<Privilege> privilegeList = userRightsService.getPrivileges(new User("rene.barjavel"), new Application("SLA"), PerimeterPath.builder().startsWith("Deal").build());
        assertions.assertThat(privilegeList).isNotEmpty();
        assertions.assertThat(privilegeList).hasSize(1);
        assertions.assertThat(privilegeList.get(0).getCode()).isEqualTo("ReadOnly");

        // Check with path Deal/GLE
        privilegeList = userRightsService.getPrivileges(new User("rene.barjavel"), new Application("SLA"), PerimeterPath.builder().startsWith("Deal").then("GLE").build());
        assertions.assertThat(privilegeList).isNotEmpty();
        assertions.assertThat(privilegeList).hasSize(2);
        assertions.assertThat(privilegeList.stream().map(p -> p.getCode()).collect(Collectors.toList())).contains("ReadWrite");

        assertions.assertAll();
    }

    @Test
    public void checkUserPrivilege() throws Exception {

        UserRights userRights = new UserRights(new User("rene.barjavel"), new Application("SLA"));
        Perimeter dealPerimeter = new Perimeter("Deal");
        Perimeter glePerimeter = new Perimeter("GLE");
        glePerimeter.getPrivileges().add(new Privilege("ReadWrite"));
        dealPerimeter.getChildren().add(glePerimeter);
        dealPerimeter.getPrivileges().add(new Privilege("ReadOnly"));
        userRights.getPerimeters().add(dealPerimeter);

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        userRightsRepositoryMock.getUserRightsList().add(userRights);

        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        SoftAssertions assertions = new SoftAssertions();

        // Check with path Deal
        boolean hasPrivilege = userRightsService.hasPrivilege(new Privilege("ReadOnly"), new User("rene.barjavel"), new Application("SLA"),
                PerimeterPath.builder().startsWith("Deal").build());
        assertions.assertThat(hasPrivilege).as("check ReadOnly for Deal").isTrue();

        hasPrivilege = userRightsService.hasPrivilege(new Privilege("ReadWrite"), new User("rene.barjavel"), new Application("SLA"),
                PerimeterPath.builder().startsWith("Deal").build());
        assertions.assertThat(hasPrivilege).as("check ReadWrite for Deal").isFalse();

        // Check with path Deal/GLE
        hasPrivilege = userRightsService.hasPrivilege(new Privilege("ReadOnly"), new User("rene.barjavel"), new Application("SLA"),
                PerimeterPath.builder().startsWith("Deal").then("GLE").build());
        assertions.assertThat(hasPrivilege).as("check ReadOnly for Deal/GLE").isTrue();

        hasPrivilege = userRightsService.hasPrivilege(new Privilege("ReadWrite"), new User("rene.barjavel"), new Application("SLA"),
                PerimeterPath.builder().startsWith("Deal").then("GLE").build());
        assertions.assertThat(hasPrivilege).as("check ReadWrite for Deal").isTrue();

        assertions.assertAll();
    }


    @Test
    public void checkAllPerimeters() throws Exception {

        UserRights userRights = new UserRights(new User("rene.barjavel"), new Application("SLA"));
        Perimeter dealPerimeter = new Perimeter("ALL", true);
        Perimeter glePerimeter = new Perimeter("GLE");
        glePerimeter.getPrivileges().add(new Privilege("ReadWrite"));
        dealPerimeter.getChildren().add(glePerimeter);
        dealPerimeter.getPrivileges().add(new Privilege("ReadOnly"));
        userRights.getPerimeters().add(dealPerimeter);

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        userRightsRepositoryMock.getUserRightsList().add(userRights);

        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        SoftAssertions assertions = new SoftAssertions();

        // Check with path Deal
        boolean hasPrivilege = userRightsService.hasPrivilege(new Privilege("ReadOnly"), new User("rene.barjavel"), new Application("SLA"),
                PerimeterPath.builder().startsWith("Deal").build());
        assertions.assertThat(hasPrivilege).as("check ReadOnly for Deal").isTrue();

        hasPrivilege = userRightsService.hasPrivilege(new Privilege("ReadWrite"), new User("rene.barjavel"), new Application("SLA"),
                PerimeterPath.builder().startsWith("Deal").build());
        assertions.assertThat(hasPrivilege).as("check ReadWrite for Deal").isFalse();

        // Check with path Deal/GLE
        hasPrivilege = userRightsService.hasPrivilege(new Privilege("ReadOnly"), new User("rene.barjavel"), new Application("SLA"),
                PerimeterPath.builder().startsWith("Deal").then("GLE").build());
        assertions.assertThat(hasPrivilege).as("check ReadOnly for Deal/GLE").isTrue();

        hasPrivilege = userRightsService.hasPrivilege(new Privilege("ReadWrite"), new User("rene.barjavel"), new Application("SLA"),
                PerimeterPath.builder().startsWith("Deal").then("GLE").build());
        assertions.assertThat(hasPrivilege).as("check ReadWrite for Deal").isTrue();

        assertions.assertAll();
    }
}
