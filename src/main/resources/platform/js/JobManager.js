var vue = new Vue({
        el:"#test",
        data: {
            //表格当前页数据
            tableData: [],

            //请求的URL
            url:'/job/queryjob',

            //默认每页数据量
            pagesize: 10,

            //当前页码
            currentPage: 1,

            //查询的页码
            start: 1,

            //默认数据总数
            totalCount: 1000,

            //添加对话框默认可见性
            dialogFormVisible: false,

            //修改对话框默认可见性
            updateFormVisible: false,

            //提交的表单
            form: {
                jobName: '',
                jobGroup: '',
                cronExpression: '',
            },

            updateform: {
                jobName: '',
                jobGroup: '',
                cronExpression: '',
            },
        },

        methods: {

            //从服务器读取数据
            loadData: function(pageNum, pageSize){
                this.$http.get('/job/queryjob?' + 'pageNum=' +  pageNum + '&pageSize=' + pageSize).then(function(res){
                    console.log(res)
                    this.tableData = res.body.JobAndTrigger.list;
                    this.totalCount = res.body.number;
                },function(){
                    console.log('failed');
                });
            },

            //单行删除
            handleDelete: function(index, row) {
                this.$http.post('/job/deletejob',{"jobClassName":row.job_NAME,"jobGroupName":row.job_GROUP},{emulateJSON: true}).then(function(res){
                    this.loadData( this.currentPage, this.pagesize);
                },function(){
                    console.log('failed');
                });
            },

            //暂停任务
            handlePause: function(index, row){
                this.$http.post('/job/pausejob',{"jobClassName":row.job_NAME,"jobGroupName":row.job_GROUP},{emulateJSON: true}).then(function(res){
                    this.loadData( this.currentPage, this.pagesize);
                },function(){
                    console.log('failed');
                });
            },

            //恢复任务
            handleResume: function(index, row){
                this.$http.post('/job/resumejob',{"jobClassName":row.job_NAME,"jobGroupName":row.job_GROUP},{emulateJSON: true}).then(function(res){
                    this.loadData( this.currentPage, this.pagesize);
                },function(){
                    console.log('failed');
                });
            },

            //搜索
            search: function(){
                this.loadData(this.currentPage, this.pagesize);
            },

            //弹出对话框
            handleadd: function(){
                this.dialogFormVisible = true;
            },

            //添加
            add: function(){
                this.$http.post('/job/addjob',{"jobClassName":this.form.jobName,"jobGroupName":this.form.jobGroup,"cronExpression":this.form.cronExpression},{emulateJSON: true}).then(function(res){
                    this.loadData(this.currentPage, this.pagesize);
                    this.dialogFormVisible = false;
                },function(){
                    console.log('failed');
                });
            },

            //更新
            handleUpdate: function(index, row){
                console.log(row)
                this.updateFormVisible = true;
                this.updateform.jobName = row.job_CLASS_NAME;
                this.updateform.jobGroup = row.job_GROUP;
                this.updateform.cronExpression = row.cron_EXPRESSION;
            },

            //更新任务
            update: function(){
                this.$http.post
                ('/job/reschedulejob',
                    {"jobClassName":this.updateform.jobName,
                        "jobGroupName":this.updateform.jobGroup,
                        "cronExpression":this.updateform.cronExpression
                    },{emulateJSON: true}
                ).then(function(res){
                    this.loadData(this.currentPage, this.pagesize);
                    this.updateFormVisible = false;
                },function(){
                    console.log('failed');
                });

            },

            //每页显示数据量变更
            handleSizeChange: function(val) {
                this.pagesize = val;
                this.loadData(this.currentPage, this.pagesize);
            },

            //页码变更
            handleCurrentChange: function(val) {
                this.currentPage = val;
                this.loadData(this.currentPage, this.pagesize);
            },

        },


    });

    //载入数据
    vue.loadData(vue.currentPage, vue.pagesize);