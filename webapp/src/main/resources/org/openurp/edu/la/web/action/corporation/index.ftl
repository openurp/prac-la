[#ftl]
[@b.head/]
[@b.toolbar title="项目单位"/]
<table class="indexpanel">
  <tr>
    <td class="index_view" >
    [@b.form name="laDepartSearchForm" action="!search" target="laDepartlist" title="ui.searchForm" theme="search"]
      [@b.textfields names="laDepart.name;名称"/]
      [@b.radios label="是否开放" name="laDepart.opened" value=laDepart.opened items="1:是,0:否" /]
      <input type="hidden" name="orderBy" value="name desc"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="laDepartlist" href="!search?orderBy=name desc"/]
    </td>
  </tr>
</table>
[@b.foot/]
