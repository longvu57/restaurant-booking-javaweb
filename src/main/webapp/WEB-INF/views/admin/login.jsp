<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="form-screen">
	<a href="index.html" class="easion-logo"><i class="fas fa-sun"></i>
		<span>Easion</span></a>
	<div class="card account-dialog">
		<div class="card-header bg-primary text-white">Please sign in</div>
		<div class="card-body">
			<form action="#!">
				<div class="form-group">
					<input type="email" class="form-control" id="exampleInputEmail1"
						aria-describedby="emailHelp" placeholder="Enter email">
				</div>
				<div class="form-group">
					<input type="password" class="form-control"
						id="exampleInputPassword1" placeholder="Password">
				</div>
				<div class="form-group">
					<div class="custom-control custom-checkbox">
						<input type="checkbox" class="custom-control-input"
							id="customCheck1"> <label class="custom-control-label"
							for="customCheck1">Remember me</label>
					</div>
				</div>
				<div class="account-dialog-actions">
					<button type="submit" class="btn btn-primary">Sign in</button>
				</div>
			</form>
		</div>
	</div>
</div>