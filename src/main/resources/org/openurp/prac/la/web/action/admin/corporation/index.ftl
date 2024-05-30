[#ftl]
[@b.head/]
[@b.toolbar title="单位基本信息管理"/]
<div class="search-container">
  <div class="search-panel">
  [@b.form name="corporationSearchForm" action="!search" target="corporationlist" title="ui.searchForm" theme="search"]
    [@b.textfields names="corporation.name;名称"/]
    <input type="hidden" name="orderBy" value="name desc"/>
  [/@]
  </div>
  <div class="search-list">[@b.div id="corporationlist" href="!search?orderBy=name desc"/]</div>
</div>
[@b.foot/]
