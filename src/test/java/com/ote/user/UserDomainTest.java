package com.ote.user;

import com.ote.JsonUtils;
import com.ote.user.api.IUserRightsService;
import com.ote.user.api.PerimeterPath;
import com.ote.user.api.exception.ApplicationNotFoundException;
import com.ote.user.api.exception.PerimeterPathNotFoundException;
import com.ote.user.api.exception.UserNotFoundException;
import com.ote.user.api.model.Perimeter;
import com.ote.user.api.model.Privilege;
import com.ote.user.api.model.UserRights;
import com.ote.user.business.UserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

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

        assertions.assertAll();
    }

    @Test
    public void testUserRightStructure() throws IOException {

        UserRights userRights = new UserRights("rene.barjavel", "SLA");
        Perimeter dealPerimeter = new Perimeter("Deal");
        Perimeter glePerimeter = new Perimeter("GLE");
        glePerimeter.getPrivileges().add(new Privilege("ReadWrite"));
        dealPerimeter.getChildren().add(glePerimeter);
        dealPerimeter.getPrivileges().add(new Privilege("ReadOnly"));
        userRights.getPerimeters().add(dealPerimeter);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(userRights).isNotNull();
        assertions.assertThat(userRights.getUser()).isEqualTo("rene.barjavel");
        assertions.assertThat(userRights.getApplication()).isEqualTo("SLA");
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

        UserRights userRights = new UserRights("rene.barjavel", "SLA");
        userRights.getPerimeters().add(new Perimeter("Deal"));

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        userRightsRepositoryMock.getUserRightsList().add(userRights);
        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        Assertions.assertThatThrownBy(() -> userRightsService.getPrivileges("NONE", "SLA", PerimeterPath.builder().startsWith("Deal").build())).
                isInstanceOf(UserNotFoundException.class);

    }

    @Test
    public void testApplicationNotFoundException() {

        UserRights userRights = new UserRights("rene.barjavel", "SLA");
        userRights.getPerimeters().add(new Perimeter("Deal"));

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        userRightsRepositoryMock.getUserRightsList().add(userRights);
        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        Assertions.assertThatThrownBy(() -> userRightsService.getPrivileges("rene.barjavel", "NONE", PerimeterPath.builder().startsWith("Deal").build())).
                isInstanceOf(ApplicationNotFoundException.class);
    }

    @Test
    public void testApplicationNotFoundExceptionForUser() {

        UserRights userRights1 = new UserRights("rene.barjavel", "GLE");
        userRights1.getPerimeters().add(new Perimeter("Deal"));

        UserRights userRights2 = new UserRights("bernard.werber", "SLA");
        userRights2.getPerimeters().add(new Perimeter("Deal"));

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        userRightsRepositoryMock.getUserRightsList().add(userRights1);
        userRightsRepositoryMock.getUserRightsList().add(userRights2);
        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        Assertions.assertThatThrownBy(() -> userRightsService.getPrivileges("rene.barjavel", "SLA", PerimeterPath.builder().startsWith("Deal").build())).
                isInstanceOf(ApplicationNotFoundException.class);
    }

    @Test
    public void testPerimeterPathIsNotDefinedForAnApplication() {

        UserRights userRights = new UserRights("rene.barjavel", "SLA");
        Perimeter dealPerimeter = new Perimeter("Deal");
        Perimeter glePerimeter = new Perimeter("GLE");
        glePerimeter.getPrivileges().add(new Privilege("ReadWrite"));
        dealPerimeter.getChildren().add(glePerimeter);
        dealPerimeter.getPrivileges().add(new Privilege("ReadOnly"));
        userRights.getPerimeters().add(dealPerimeter);

        UserRightsRepositoryMock userRightsRepositoryMock = new UserRightsRepositoryMock();
        userRightsRepositoryMock.getUserRightsList().add(userRights);

        IUserRightsService userRightsService = new UserRightsService(userRightsRepositoryMock);

        Assertions.assertThatThrownBy(() -> userRightsService.getPrivileges("rene.barjavel", "SLA", PerimeterPath.builder().startsWith("Deal").then("NONE").build())).
                isInstanceOf(PerimeterPathNotFoundException.class);
    }

    @Test
    public void testPerimeterPathIsDefinedForAnApplication() throws Exception {

        UserRights userRights = new UserRights("rene.barjavel", "SLA");
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
        List<Privilege> privilegeList = userRightsService.getPrivileges("rene.barjavel", "SLA", PerimeterPath.builder().startsWith("Deal").build());
        assertions.assertThat(privilegeList).isNotEmpty();
        assertions.assertThat(privilegeList).hasSize(1);
        assertions.assertThat(privilegeList.get(0).getCode()).isEqualTo("ReadOnly");

        // Check with path Deal/GLE
        privilegeList = userRightsService.getPrivileges("rene.barjavel", "SLA", PerimeterPath.builder().startsWith("Deal").then("GLE").build());
        assertions.assertThat(privilegeList).isNotEmpty();
        assertions.assertThat(privilegeList).hasSize(1);
        assertions.assertThat(privilegeList.get(0).getCode()).isEqualTo("ReadWrite");

        assertions.assertAll();
    }

    @Test
    public void checkUserPrivilege() throws Exception {

        UserRights userRights = new UserRights("rene.barjavel", "SLA");
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
        boolean hasPrivilege = userRightsService.hasPrivilege("ReadOnly", "rene.barjavel", "SLA", PerimeterPath.builder().startsWith("Deal").build());
        assertions.assertThat(hasPrivilege).isTrue();

        hasPrivilege = userRightsService.hasPrivilege("ReadWrite", "rene.barjavel", "SLA", PerimeterPath.builder().startsWith("Deal").build());
        assertions.assertThat(hasPrivilege).isFalse();

        // Check with path Deal/GLE
        hasPrivilege = userRightsService.hasPrivilege("ReadOnly", "rene.barjavel", "SLA", PerimeterPath.builder().startsWith("Deal").then("GLE").build());
        assertions.assertThat(hasPrivilege).isFalse();

        hasPrivilege = userRightsService.hasPrivilege("ReadWrite", "rene.barjavel", "SLA", PerimeterPath.builder().startsWith("Deal").then("GLE").build());
        assertions.assertThat(hasPrivilege).isTrue();

        assertions.assertAll();
    }

}
