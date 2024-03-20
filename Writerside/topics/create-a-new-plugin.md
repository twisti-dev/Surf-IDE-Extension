# Create a new plugin

## Before you start

Before you start creating a new plugin, make sure you think about the following:

- **Name**: A project name that is unique and easy to remember. It should be in lowercase and words
  should be separated by `-`.
- **Dependencies**: Wich dependencies do you need? Do you need `surf-data` and `surf-api`?
    - **Version**: Which version of the dependencies do you need?
- **Plugin modules**: Which modules do you need? You can select `api`, `core`, `bukkit`
  and `velocity`.
- **Group ID**: The group ID is a unique identifier for your project. It is used to identify your
  project when it is built and deployed. For example, `dev.slne.surf`.
- **Gradle version**: Which version of Gradle do you need?

## Create a new plugin

<procedure>
<step>

Launch IntelliJ IDEA.

If the Welcome screen opens, click <ui-path>New Project</ui-path>. Otherwise, go to
<ui-path>File | New | Project</ui-path> in the main menu.

</step>
<step>

From the list on the left, select <ui-path>Surf Module</ui-path>.

</step>
<step>

Name the new project and change its location if necessary.

</step>
<step>

Select the <ui-path>Create Git repository</ui-path> checkbox to place the new project under version
control.

You will be able to do it later at any time.

</step>
<step>

Select the dependencies you need. You can select `SurfData` and `SurfAPI`.

You may also change the version of the dependencies if necessary.

</step>
<step>

Select the modules you need. You can select `api`, `core`, `bukkit` and `velocity`.

</step>
<step>

Set the **Base Module Name** and **File Project Name**

> The **Base Module Name** acts like a prefix for the plugin modules. For example, if you set the
> **Base Module Name** to `surf`, the plugin modules will be
> named `surf-api`, `surf-core`, `surf-bukkit` and `surf-velocity`.
>
{title="Base Module Name"}

> The **File Project Name** is used to create file names. For example, if you set the 
> **File Project Name** to `Surf`, the api instance will be named `SurfInstance` and the
> core instance will be named `SurfCoreInstance` and so on.
> 
{title="File Project Name"}

</step>
<step>

Set the **Group ID** or leave the default value.

</step>
<step>

Set the **Artifact ID** or leave the default value.

</step>
<step>

Set the **Gradle Version** or leave the default value.

</step>
<step>

Click <ui-path>Create</ui-path>.

</step>
</procedure>