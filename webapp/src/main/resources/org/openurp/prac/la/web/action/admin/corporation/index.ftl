[#ftl]
[@b.head/]
[@b.toolbar title="单位基本信息管理"/]
<table class="indexpanel">
  <tr>
    <td class="index_view" >
    [@b.form name="corporationSearchForm" action="!search" target="corporationlist" title="ui.searchForm" theme="search"]
      [@b.textfields names="corporation.name;名称"/]
      <input type="hidden" name="orderBy" value="name desc"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="corporationlist" href="!search?orderBy=name desc"/]
    </td>
  </tr>
</table>
[@b.foot/]
